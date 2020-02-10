package org.bravo.model.json

import org.bravo.model.db.Users
import org.jetbrains.exposed.sql.ResultRow

data class Users(
    val username: String,
    val password: String
) {
    companion object {
        fun fromResultRow(row: ResultRow) = Users(row[Users.username], row[Users.password])
    }
}
