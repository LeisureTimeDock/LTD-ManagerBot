package top.r3944realms.ltdmanager.utils

import org.snakeyaml.engine.v2.api.Dump
import org.snakeyaml.engine.v2.api.DumpSettings
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import org.snakeyaml.engine.v2.common.FlowStyle
import org.snakeyaml.engine.v2.nodes.*
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

object YamlUpdater {
    /**
     * 更新 YAML 文件字段值（不保留注释）
     * @param filePath  YAML 文件路径
     * @param keyPath   层级字段路径（如 "database.url"）
     * @param newValue  新值
     */
    @Throws(Exception::class)
    fun updateYaml(filePath: String, keyPath: String, newValue: Any?) {
        val normalizedPath = normalizePath(filePath)
        val content = try {
            Files.readString(Paths.get(normalizedPath))
                .takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("YAML 文件为空")
        } catch (e: NoSuchFileException) {
            throw IllegalStateException("文件不存在: $normalizedPath")
        }

        val settings = LoadSettings.builder()
            .setLabel("YAML 配置文件")
            .build()

        val loader = Load(settings)
        val parsed = loader.loadFromString(content)

        if (parsed !is MutableMap<*, *>) {
            throw IllegalStateException("文档根节点必须是 Map，实际是: ${parsed?.javaClass?.name}")
        }

        @Suppress("UNCHECKED_CAST")
        val root = parsed as MutableMap<String, Any?>

        // 更新节点
        updateMap(root, keyPath.split('.'), newValue)

        // 保存
        val dumpSettings = DumpSettings.builder()
            .setDefaultFlowStyle(FlowStyle.BLOCK)
            .setIndent(2)
            .setWidth(120)
            .build()

        Files.writeString(Paths.get(normalizedPath), Dump(dumpSettings).dumpToString(root))
    }

    private fun updateMap(map: MutableMap<String, Any?>, keys: List<String>, value: Any?) {
        if (keys.size == 1) {
            map[keys[0]] = value
        } else {
            val child = map[keys[0]]
            if (child !is MutableMap<*, *>) {
                throw IllegalArgumentException("Invalid path: ${keys[0]}")
            }
            @Suppress("UNCHECKED_CAST")
            updateMap(child as MutableMap<String, Any?>, keys.drop(1), value)
        }
    }
    private fun normalizePath(path: String): String =
        path.replace("^/([A-Za-z]:/)".toRegex(), "$1")
}

