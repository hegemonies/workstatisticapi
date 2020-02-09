package org.bravo.model

import org.jetbrains.exposed.dao.LongIdTable

object Users : LongIdTable() {
    val username = text("username")
    val password = text("password")
}
