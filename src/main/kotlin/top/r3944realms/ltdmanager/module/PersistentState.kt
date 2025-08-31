package top.r3944realms.ltdmanager.module

import java.io.File

interface PersistentState<T> {
    fun getStateFile(): File
    fun getState(): T
    fun saveState(state: T)
    fun loadState(): T
}