plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23" // 添加序列化插件
    application
}

group = "top.r3944realms.ltdmanager"
version = "1.0-SNAPSHOT"

repositories {

    repositories {
        mavenLocal()
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        mavenCentral()
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
    }
//TODO: 0872d1c0-829c-e1d7-6782-89e45c8a6b76
    dependencies {
        // 添加序列化库
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

        // Ktor WebSocket客户端
        implementation("io.ktor:ktor-client-websockets:2.3.3")
        implementation("io.ktor:ktor-client-cio:2.3.3")
        implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3") // 推荐使用kotlinx.serialization替代Gson

        // 数据库相关
        implementation("org.jetbrains.exposed:exposed-core:0.41.1")
        implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
        implementation("com.mysql:mysql-connector-j:8.0.33") // 使用MySQL 8.x驱动
        implementation("com.zaxxer:HikariCP:5.0.1") // 连接池

        // 日志系统
        implementation("org.slf4j:slf4j-api:2.0.7")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
        implementation("org.apache.logging.log4j:log4j-core:2.20.0")
        implementation("org.apache.logging.log4j:log4j-api:2.20.0")

        // 配置管理
        implementation("org.yaml:snakeyaml:2.2")
        implementation("com.typesafe:config:1.4.2") // 类型安全的配置库

        // 协程
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

        // 测试
        testImplementation(kotlin("test"))
        testImplementation("io.ktor:ktor-client-mock:2.3.3")
    }

    tasks.test {
        useJUnitPlatform()
    }
    kotlin {
        jvmToolchain(17)
    }
    application {
        mainClass.set("top.r3944realms.ltdmanager.main") // 设置主类
    }
}