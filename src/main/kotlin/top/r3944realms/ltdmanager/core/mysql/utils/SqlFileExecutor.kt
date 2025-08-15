package top.r3944realms.ltdmanager.core.mysql.utils

import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.SQLException

class SqlFileExecutor private constructor() {
    companion object {
        private val log = LoggerFactory.getLogger(SqlFileExecutor::class.java)
        /**
         * 执行SQL语句
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`module`模块下的`resources/`目录下的`sql/init.sql`
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFile(conn: Connection, filePath: String, module: Module) {
            readAndExecuteSql(conn, filePath, -1, module)
        }

        /**
         * 执行SQL语句
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`resources/`目录下的`sql/init.sql`
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFile(conn: Connection, filePath: String) {
            readAndExecuteSql(conn, filePath, -1)
        }

        /**
         * 执行SQL语句（带批处理）
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`resources/`目录下的`sql/init.sql`
         * @param batchSize 批处理语句数
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFile(conn: Connection, filePath: String, batchSize: Int) {
            readAndExecuteSql(conn, filePath, batchSize)
        }

        /**
         * 执行SQL语句（带批处理）
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`module`模块下的`resources/`目录下的`sql/init.sql`
         * @param batchSize 批处理语句数
         * @param module 资源所属模块
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFile(conn: Connection, filePath: String, batchSize: Int, module: Module) {
            readAndExecuteSql(conn, filePath, batchSize, module)
        }

        /**
         * 执行SQL语句（带批处理和事务）
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`resources/`目录下的`sql/init.sql`
         * @param batchSize 批处理语句数
         * @param module 资源所属模块
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFileWithTransactional(conn: Connection, filePath: String, batchSize: Int, module: Module) {
            val originalAutoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                readAndExecuteSql(conn, filePath, batchSize, module)
                conn.commit()
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = originalAutoCommit
            }
        }

        /**
         * 执行SQL语句（带事务）
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`module`模块下的`resources/`目录下的`sql/init.sql`
         * @param module 资源所属模块
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFileWithTransactional(conn: Connection, filePath: String, module: Module) {
            executeSqlFileWithTransactional(conn, filePath, -1, module)
        }

        /**
         * 执行SQL语句（带批处理和事务）
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`resources/`目录下的`sql/init.sql`
         * @param batchSize 批处理语句数
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFileWithTransactional(conn: Connection, filePath: String, batchSize: Int) {
            executeSqlFileWithTransactional(conn, filePath, batchSize, SqlFileExecutor::class.java.module)
        }

        /**
         * 执行SQL语句（带事务）
         * @param conn 数据库连接
         * @param filePath 文件路径 如`/sql/init.sql`指`resources/`目录下的`sql/init.sql`
         * @throws SQLException SQL语句执行出问题
         * @throws IOException 对应文件缺失或不可读
         */
        @Throws(IOException::class, SQLException::class)
        fun executeSqlFileWithTransactional(conn: Connection, filePath: String) {
            executeSqlFileWithTransactional(conn, filePath, -1, SqlFileExecutor::class.java.module)
        }

        private fun readAndExecuteSql(conn: Connection, filePath: String, batchSize: Int) {
            readAndExecuteSql(conn, filePath, batchSize, SqlFileExecutor::class.java.module)
        }

        private fun readAndExecuteSql(
            conn: Connection,
            filePath: String,
            batchSize: Int,
            module: Module
        ) {
            val inputStream = module.getResourceAsStream(filePath) ?: throw IOException("SQL file not found: $filePath")

            BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                conn.createStatement().use { stmt ->
                    val useBatch = batchSize > 0
                    var count = 0
                    val sqlBuilder = StringBuilder()

                    reader.lineSequence()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && !it.startsWith("--") }
                        .forEach { line ->
                            sqlBuilder.append(line).append(" ")
                            if (line.endsWith(";")) {
                                val sql = sqlBuilder.substring(0, sqlBuilder.length - 1).trim()
                                try {
                                    if (useBatch) {
                                        stmt.addBatch(sql)
                                        sqlBuilder.clear()
                                        count++
                                        if (count % batchSize == 0) stmt.executeBatch()
                                    } else {
                                        stmt.execute(sql)
                                    }
                                } catch (e: Exception) {
                                    log.error("执行SQL失败: {}", sql, e)
                                    throw e
                                }
                                sqlBuilder.clear()
                            }
                        }

                    // Handle remaining SQL without semicolon
                    if (sqlBuilder.isNotEmpty()) {
                        val sql = sqlBuilder.toString().trim()
                        try {
                            if (useBatch) {
                                stmt.addBatch(sql)
                                stmt.executeBatch()
                            } else {
                                stmt.execute(sql)
                            }
                        } catch (e: SQLException) {
                            log.error("执行最后一条SQL失败: {}", sql, e)
                            throw e
                        }
                    }

                    // Execute remaining batch
                    if (useBatch && count % batchSize != 0) {
                        stmt.executeBatch()
                    }
                }
            }
        }
    }
}