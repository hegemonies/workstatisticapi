package org.bravo.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow

object Users : LongIdTable() {
    val username = text("username")
    val password = text("password")

    private val jsonMapper = jacksonObjectMapper()

    fun toJson(row: ResultRow) = jsonMapper.writeValueAsString(UsersJson.fromResultRow(row))
    fun toJson(query: Query) = jsonMapper.writeValueAsString(UsersJson.fromQuery(query))
    fun toList(query: Query) = query.map {
        UsersJson.fromResultRow(it)
    }
}

data class UsersJson private constructor(
    val username: String,
    val password: String
) {
    companion object {
        fun fromResultRow(row: ResultRow) =
            UsersJson(
                row[Users.username],
                row[Users.password]
            )

        fun fromQuery(query: Query) =
            query.map {
                fromResultRow(it)
            }.toList()
    }
}
