package org.bravo.settings

import org.bravo.model.Statistic
import org.bravo.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class DatabaseSettings {

    init {
        logger.info("Bootstrap database")
        Database.connect(
            "jdbc:postgresql://localhost:5432/work_statistic",
            driver = "org.postgresql.Driver",
            user = "postgres", password = "password"
        )
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Users)
            SchemaUtils.create(Statistic)
            // Users.insert {
            //     it[username] = "admin"
            //     it[password] = "password"
            // }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
