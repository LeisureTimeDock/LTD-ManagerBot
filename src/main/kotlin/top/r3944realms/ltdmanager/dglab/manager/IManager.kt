package top.r3944realms.ltdmanager.dglab.manager

interface IManager<T> {
    fun startAll()
    fun stopAll()
    fun getInstance(): T?
}