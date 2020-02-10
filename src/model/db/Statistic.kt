package org.bravo.model.db

import org.jetbrains.exposed.dao.LongIdTable

object Statistic : LongIdTable() {
    val userId = long("user_id").references(Users.id).index()
    val start = date("start")
    val end = date("end")
}
