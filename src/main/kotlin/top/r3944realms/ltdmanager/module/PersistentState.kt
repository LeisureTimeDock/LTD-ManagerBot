package top.r3944realms.ltdmanager.module

import java.io.File

interface PersistentState<T> {
    fun getStateFile(): File
    fun getState(): T
    fun saveState(state: T)
    fun loadState(): T
    // 默认实现：统一管理 data 目录下的文件
    fun getStateFile(name: String): File {
        val dataDir = File("data")
        if (!dataDir.exists()) dataDir.mkdirs()
        return File(dataDir, name)
    }
}