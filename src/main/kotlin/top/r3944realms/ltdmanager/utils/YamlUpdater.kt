package top.r3944realms.ltdmanager.utils

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream
import java.io.FileWriter
import java.io.IOException

object YamlUpdater {
    /**
     * 更新 YAML 文件字段值，保留原始格式
     * @param filePath     YAML 文件路径
     * @param keyPath     层级字段路径（如 "database.url"）
     * @param newValue    新值
     */
    @Throws(IOException::class)
    fun updateYamlValue(filePath: String, keyPath: String, newValue: String) {
        // 1. 读取原始 YAML 文件
        // 标准化路径
        val normalizedPath = filePath.replaceFirst("^/(.:/)".toRegex(), "$1") // 修复Windows路径
        val yaml = Yaml()
        val yamlData: Map<String, Any>
        FileInputStream(normalizedPath).use { inputStream ->
            yamlData = yaml.load(inputStream)
        }

        // 2. 更新嵌套 Map 中的值
        updateNestedValue(yamlData, keyPath.split("\\.".toRegex()).toTypedArray(), 0, newValue)

        // 3. 配置 YAML 输出格式（保留原始风格）
        val options = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.FLOW // 使用 {} 风格
            indent = 2 // 缩进2空格
            isPrettyFlow = true // 保持可读性
        }

        // 4. 写回文件
        FileWriter(normalizedPath).use { writer ->
            Yaml(options).dump(yamlData, writer)
        }
    }
    private fun updateNestedValue(map: Map<String, Any>, keys: Array<String>, index: Int, newValue: Any) {
        if (index == keys.size - 1) {
            (map as MutableMap<String, Any>)[keys[index]] = newValue // 更新最终字段
        } else {
            val nested = map[keys[index]]
            if (nested is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val nestedMap = nested as Map<String, Any>
                updateNestedValue(nestedMap, keys, index + 1, newValue)
            } else {
                throw IllegalArgumentException("Invalid YAML path: ${keys.joinToString(".")}")
            }
        }
    }
}