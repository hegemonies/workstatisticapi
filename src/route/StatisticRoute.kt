package org.bravo.route

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.bravo.model.Statistic
import org.bravo.model.StatisticJson
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StatisticRoute")

fun Route.statistic() {
    post("/addStatistic") {
        val statistic = call.receive<StatisticJson>()
        logger.info("Receive statistic: $statistic")
        transaction {
            Statistic.insert {
                it[userId] = 1
                it[start] = statistic.start
                it[end] = statistic.end
            }
        }
        call.respond(statistic)
    }

    get("/getStatistic") {
        newSuspendedTransaction {
            Statistic.selectAll().let {
                Statistic.toList(it)
            }.also {
                call.respond(it)
            }
        }
    }
}
