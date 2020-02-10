package org.bravo.model.db

import org.jetbrains.exposed.dao.LongIdTable

object Users : LongIdTable() {
    val username = text("username")
    val password = text("password")
}
