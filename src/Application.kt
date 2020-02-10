package org.bravo

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bravo.model.db.Users
import org.bravo.settings.DatabaseSettings
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.lang.Exception

fun main(args: Array<String>): Unit {
    // println(Date.from(Instant.ofEpochMilli(System.currentTimeMillis())))
    DatabaseSettings()
    EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    // install(DropwizardMetrics) {
    //     val reporter = Slf4jReporter.forRegistry(registry)
    //         .outputTo(log)
    //         .convertRatesTo(TimeUnit.SECONDS)
    //         .convertDurationsTo(TimeUnit.MILLISECONDS)
    //         .build();
    //     reporter.start(10, TimeUnit.SECONDS);
    // }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized, cause.message ?: "")
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden, cause.message ?: "")
            }
            exception<Exception> { cause ->
                call.respond(HttpStatusCode.InternalServerError, cause.message ?: "")
            }
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        post("/addUser") {
            val user = call.receive<org.bravo.model.json.Users>()
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
            call.respond(user)
        }

        get("/users") {
            mutableListOf<org.bravo.model.json.Users>().also { list ->
                transaction {
                    Users.selectAll().toList().forEach { row ->
                        list.add(org.bravo.model.json.Users.fromResultRow(row))
                    }
                }
                call.respond(list)
            }
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
