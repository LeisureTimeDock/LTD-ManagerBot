package top.r3944realms.ltdmanager.core.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import top.r3944realms.ltdmanager.core.config.YamlConfigLoader
import java.sql.Connection
import java.sql.SQLException

class MysqlHikariConnectPool : AutoCloseable {
    private val dataSource: HikariDataSource
    constructor() {
        val config = HikariConfig().apply {
            jdbcUrl = YamlConfigLoader.loadDatabaseConfig().url
            username = YamlConfigLoader.loadDatabaseConfig().user
            password = YamlConfigLoader.loadDatabaseConfig().decryptedPassword
            // 8.0+ 推荐配置
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        }
        dataSource = HikariDataSource(config)
    }
    constructor(hikariConfig: HikariConfig) {
        dataSource = HikariDataSource(hikariConfig)
    }
    /**
    * 获取数据库连接
    * @return 连接
    * @throws SQLException SQL异常
    */
    @Throws(SQLException::class)
    fun getConnection(): Connection {
        return dataSource.connection
    }

    override fun close() {
        if (!dataSource.isClosed) {
            dataSource.close()
        }
    }
}