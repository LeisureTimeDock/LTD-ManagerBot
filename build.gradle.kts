import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun k(v: String) = project.property(v) as String

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23" // 添加序列化插件
    application
    id("com.github.johnrengelman.shadow") version "8.0.0" // fat jar
}

group = k("project_group")
version = k("project_version")

repositories {

    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        mavenCentral()
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        maven {
            url = uri("https://libraries.minecraft.net/")
        }
        // 第三方 repo，比如 MohistMC 或 GlareMasters Pub
        maven {
            url = uri("https://repo.glaremasters.me/repository/public/")
        }
        maven {
            name = "LTD Maven"
            url = uri("https://nexus.bot.leisuretimedock.top/repository/maven-public/")
        }
    }
//TODO: 0872d1c0-829c-e1d7-6782-89e45c8a6b76
    dependencies {
        // 添加序列化库
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

        // Ktor WebSocket客户端
        implementation("io.ktor:ktor-client-core:2.3.12")
        implementation("io.ktor:ktor-client-websockets:2.3.3")
        implementation("io.ktor:ktor-client-cio:2.3.3")
        implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3") // 推荐使用kotlinx.serialization替代Gson
        implementation("io.ktor:ktor-client-content-negotiation:2.3.12")

        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        // 如果需要日志拦截器（推荐用于调试）
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

        // 数据库相关
        implementation("org.jetbrains.exposed:exposed-core:0.41.1")
        implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
        implementation("com.mysql:mysql-connector-j:8.0.33") // 使用MySQL 8.x驱动
        implementation("com.zaxxer:HikariCP:5.0.1") // 连接池

        // 邮箱相关
        implementation("jakarta.mail:jakarta.mail-api:2.0.1") //API
        implementation("com.sun.mail:jakarta.mail:2.0.1")    // 实现

        // 日志系统
        implementation("org.slf4j:slf4j-api:2.0.7")
        implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
        implementation("org.apache.logging.log4j:log4j-core:2.20.0")
        implementation("org.apache.logging.log4j:log4j-api:2.20.0")

        // 配置管理
        implementation("org.yaml:snakeyaml:2.4")
        implementation("org.snakeyaml:snakeyaml-engine:2.10")
        implementation("com.typesafe:config:1.4.2") // 类型安全的配置库

        // 协程
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        implementation("org.apache.commons:commons-lang3:3.17.0")
        implementation("com.google.guava:guava:33.3.0-jre")

        //DG_Lab 依赖库导入
        implementation("io.netty:netty-all:4.1.109.Final")
        implementation("com.google.code.gson:gson:2.10.1")
        implementation("top.r3944realms.dg_lab:Common:${k("dg_lab_version")}")

        //生成 二维码
        implementation("com.google.zxing:core:[3.5.3,)")

        //命令解析
        implementation("com.mojang:brigadier:1.2.9")

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
        mainClass.set("top.r3944realms.ltdmanager.MainKt") // 设置主类
    }
}
tasks {
    // ShadowJar 配置
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("") // 去掉 -all 后缀
        mergeServiceFiles()
        manifest {
            attributes["Main-Class"] = "top.r3944realms.ltdmanager.MainKt"
        }
    }

    // build 依赖 shadowJar
    build {
        dependsOn("shadowJar")
    }
}