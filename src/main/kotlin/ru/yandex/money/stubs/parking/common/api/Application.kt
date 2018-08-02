package ru.yandex.money.stubs.parking.common.api

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import ru.yandex.money.stubs.parking.common.api.auth.AuthorizationException
import ru.yandex.money.stubs.parking.common.api.auth.authenticated
import ru.yandex.money.stubs.parking.common.api.auth.commonTokenRegistry
import ru.yandex.money.stubs.parking.common.api.json.JsonConverter
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException
import ru.yandex.money.stubs.parking.common.api.process.status.Status
import ru.yandex.money.stubs.parking.common.api.process.status.StatusService
import ru.yandex.money.stubs.parking.common.api.process.token.AuthorizationService
import ru.yandex.money.stubs.parking.common.api.process.token.CredentialsRegistry
import ru.yandex.money.stubs.parking.common.api.process.token.TokenCredentials
import ru.yandex.money.stubs.parking.common.api.process.token.TokenRegistry
import java.time.ZonedDateTime

fun main(args: Array<String>) {
    val jsonConverter = JsonConverter(
            mapOf(
                    TokenCredentials::class to TokenCredentials.Deserializer
            )
    )

    val tokenRegistry = object : TokenRegistry {
        private val tokens = mutableMapOf<String, ZonedDateTime>()

        override fun create(token: String, expiresAt: ZonedDateTime) {
            tokens[token] = expiresAt
        }

        override fun contains(token: String): Boolean {
            val expiresAt = tokens[token]
            return expiresAt != null && expiresAt.isAfter(ZonedDateTime.now())
        }
    }

    val authorizationService = AuthorizationService(
            object : CredentialsRegistry {
                private val credentials = setOf(
                        TokenCredentials("abra", "cadabra")
                )

                override fun resolve(credentials: TokenCredentials) = credentials in this.credentials
            },
            tokenRegistry
    )

    val statusService = object : StatusService {
        override fun status() = Status(true)
    }

    commonTokenRegistry = tokenRegistry

    val server = embeddedServer(Netty, port = 8080) {
        install(DefaultHeaders)
        install(CallLogging)
        install(ContentNegotiation) {
            register(ContentType.Application.Json, jsonConverter)
        }

        install(StatusPages) {
            exception<ApplicationException> {
                context.respond(HttpStatusCode.BadRequest, it.error)
            }

            exception<AuthorizationException> {
                context.respond(HttpStatusCode.Unauthorized, ApplicationError.ExpiredToken)
            }
        }

        routing {
            accept(ContentType.Application.Json) {
                route("/v1") {
                    post("/token") {
                        val credentials = call.receive<TokenCredentials>()
                        val token = authorizationService.createToken(credentials)
                        call.respond(token)
                    }

                    authenticated {
                        post("/status") {
                            call.respond(statusService.status())
                        }
                    }
                }
            }
        }
    }
    server.start(wait = true)
}
