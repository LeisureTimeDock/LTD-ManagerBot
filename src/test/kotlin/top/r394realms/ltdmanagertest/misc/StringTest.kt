package top.r394realms.ltdmanagertest.misc

fun main() {
    val test = "/s 222"
    val startsWith = test.startsWith("/s")
    var removePrefix = "";
    if (startsWith) {
        removePrefix = test.removePrefix("/s")
    }
    println(startsWith)
    println(removePrefix)
}