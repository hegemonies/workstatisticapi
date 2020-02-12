package org.bravo.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.exposed.dao.LongIdTable
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.joda.time.DateTime

object Statistic : LongIdTable() {
    val userId = long("user_id").references(Users.id).index()
    val start = long("start")
    val end = long("end")

    private val jsonMapper = jacksonObjectMapper()

    fun toJson(row: ResultRow) = jsonMapper.writeValueAsString(StatisticJson.fromResultRow(row))
    fun toJson(query: Query) = jsonMapper.writeValueAsString(StatisticJson.fromQuery(query))
    fun toList(query: Query) = query.map {
        StatisticJson.fromResultRow(it)
    }
}

data class StatisticJson(
    val start: Long,
    val end: Long
) {
    companion object {
        fun fromResultRow(row: ResultRow) =
            StatisticJson(
                row[Statistic.start],
                row[Statistic.end]
            )

        fun fromQuery(query: Query) =
            query.map {
                fromResultRow(it)
            }.toList()
    }
}
