package top.r3944realms.ltdmanager.module

import top.r3944realms.ltdmanager.utils.FileNameFilter
import java.io.File

interface PersistentState<T> {
    fun getStateFileInternal(): File
    fun getState(): T
    fun saveState(state: T)
    fun loadState(): T
    // 默认实现：统一管理 data 目录下的文件
    fun getStateFileInternal(name: String, subName: String): File {
        val dataDir = File("data", FileNameFilter.filterFileName(subName))
        if (!dataDir.exists()) dataDir.mkdirs()
        return File(dataDir, name)
    }
}