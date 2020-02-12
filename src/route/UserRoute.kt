package org.bravo.route

import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.bravo.model.Users
import org.bravo.model.UsersJson
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("UserRoute")

fun Route.users() {
    post("/addUser") {
        val user = call.receive<UsersJson>()
        transaction {
            val users = Users.select {
                (Users.username eq user.username) and (Users.password eq user.password)
            }.toList()
            if (users.isNotEmpty()) {
                println("Such ${user.username} already exists")
            } else {
                Users.insertIgnore {
                    it[username] = user.username
                    it[password] = user.password
                }
            }
        }
        logger.info("Add new user: $user")
        call.respond(user)
    }

    get("/users") {
        newSuspendedTransaction {
            Users.selectAll().let {
                Users.toList(it)
            }.also {
                call.respond(it)
            }
        }
    }
}
