package top.r3944realms.ltdmanager.utils

/**
 * 文件名非法字符过滤器
 */
object FileNameFilter {

    // Windows系统非法字符
    private val WINDOWS_ILLEGAL_CHARS = setOf('\\', '/', ':', '*', '?', '"', '<', '>', '|')

    // Unix系统非法字符（主要是/和空字符）
    private val UNIX_ILLEGAL_CHARS = setOf('/', '\u0000')

    // 通用非法字符（控制字符）
    private val CONTROL_CHARS = (0x00..0x1F).map { it.toChar() }.toSet()

    // Windows保留文件名
    private val WINDOWS_RESERVED_NAMES = setOf(
        "CON", "PRN", "AUX", "NUL",
        "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
        "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    )

    /**
     * 过滤文件名中的非法字符
     * @param fileName 原始文件名（不包含路径）
     * @param systemType 目标系统类型
     * @param replaceChar 替换字符
     * @param handleReservedNames 是否处理保留名称
     * @return 过滤后的安全文件名
     */
    fun filterFileName(
        fileName: String,
        systemType: SystemType = SystemType.CROSS_PLATFORM,
        replaceChar: Char = '_',
        handleReservedNames: Boolean = true
    ): String {
        if (fileName.isEmpty()) return fileName

        var filtered = when (systemType) {
            SystemType.WINDOWS -> filterForWindows(fileName, replaceChar)
            SystemType.UNIX -> filterForUnix(fileName, replaceChar)
            SystemType.CROSS_PLATFORM -> filterCrossPlatform(fileName, replaceChar)
        }

        // 处理保留名称
        if (handleReservedNames && systemType != SystemType.UNIX) {
            filtered = handleReservedName(filtered, replaceChar)
        }

        // 确保文件名不以点或空格结尾（某些系统有问题）
        filtered = filtered.trimEnd('.', ' ')

        // 如果过滤后为空，返回默认名称
        return if (filtered.isEmpty()) "unnamed_file" else filtered
    }

    /**
     * 为Windows系统过滤
     */
    private fun filterForWindows(fileName: String, replaceChar: Char): String {
        val illegalChars = WINDOWS_ILLEGAL_CHARS + CONTROL_CHARS
        return fileName.map { if (it in illegalChars) replaceChar else it }.joinToString("")
    }

    /**
     * 为Unix系统过滤
     */
    private fun filterForUnix(fileName: String, replaceChar: Char): String {
        val illegalChars = UNIX_ILLEGAL_CHARS + CONTROL_CHARS
        return fileName.map { if (it in illegalChars) replaceChar else it }.joinToString("")
    }

    /**
     * 跨平台过滤
     */
    private fun filterCrossPlatform(fileName: String, replaceChar: Char): String {
        val illegalChars = WINDOWS_ILLEGAL_CHARS + UNIX_ILLEGAL_CHARS + CONTROL_CHARS
        return fileName.map { if (it in illegalChars) replaceChar else it }.joinToString("")
    }

    /**
     * 处理Windows保留名称
     */
    private fun handleReservedName(fileName: String, replaceChar: Char): String {
        val nameWithoutExt = fileName.substringBeforeLast('.')
        val extension = fileName.substringAfterLast('.', "")

        return if (WINDOWS_RESERVED_NAMES.contains(nameWithoutExt.uppercase())) {
            val newName = "${nameWithoutExt}$replaceChar"
            if (extension.isNotEmpty()) "$newName.$extension" else newName
        } else {
            fileName
        }
    }

    /**
     * 批量处理文件名
     */
    fun batchFilterFileNames(
        fileNames: List<String>,
        systemType: SystemType = SystemType.CROSS_PLATFORM,
        replaceChar: Char = '_'
    ): Map<String, String> {
        return fileNames.associateWith { filterFileName(it, systemType, replaceChar) }
    }

    /**
     * 验证文件名是否安全
     */
    fun isFileNameSafe(
        fileName: String,
        systemType: SystemType = SystemType.CROSS_PLATFORM
    ): Boolean {
        if (fileName.isEmpty()) return false

        val filtered = filterFileName(fileName, systemType, '_', true)
        return fileName == filtered && fileName == filtered.trimEnd('.', ' ')
    }
}