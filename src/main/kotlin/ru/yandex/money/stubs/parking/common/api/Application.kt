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
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.dizitart.no2.Document
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import org.dizitart.no2.tool.Exporter
import ru.yandex.money.stubs.parking.common.api.accounts.NitriteAccountsRegistry
import ru.yandex.money.stubs.parking.common.api.auth.AuthorizationException
import ru.yandex.money.stubs.parking.common.api.auth.authenticated
import ru.yandex.money.stubs.parking.common.api.auth.commonTokenRegistry
import ru.yandex.money.stubs.parking.common.api.data.DataRequest
import ru.yandex.money.stubs.parking.common.api.data.DataService
import ru.yandex.money.stubs.parking.common.api.json.JsonConverter
import ru.yandex.money.stubs.parking.common.api.process.balance.BalanceRequest
import ru.yandex.money.stubs.parking.common.api.process.balance.BalanceService
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException
import ru.yandex.money.stubs.parking.common.api.process.status.Status
import ru.yandex.money.stubs.parking.common.api.process.status.StatusService
import ru.yandex.money.stubs.parking.common.api.process.token.AuthorizationService
import ru.yandex.money.stubs.parking.common.api.process.token.NitriteCredentialsRegistry
import ru.yandex.money.stubs.parking.common.api.process.token.NitriteTokenRegistry
import ru.yandex.money.stubs.parking.common.api.process.token.TokenCredentials
import java.io.StringWriter

fun main(args: Array<String>) {
    val jsonConverter = JsonConverter(
            mapOf(
                TokenCredentials::class to TokenCredentials.Deserializer,
                BalanceRequest::class to BalanceRequest.Deserializer,
                DataRequest::class to DataRequest.Deserializer
            )
    )

    val db = Nitrite.builder()
        .compressed()
        .filePath("./common-api-stub.db")
        .openOrCreate()

    val tokenRegistry = NitriteTokenRegistry(db)
    val credentialsRegistry = NitriteCredentialsRegistry(db) {
        if (it.find(Filters.eq("login", "abra")).size() == 1) {
            return@NitriteCredentialsRegistry
        }

        val default = Document.createDocument("login", "abra")
            .put("password", "cadabra")

        val result = it.insert(default)
        if (result.affectedCount != 1) {
            throw IllegalStateException("Unable to insert initial credentials")
        }
    }

    val authorizationService = AuthorizationService(
            credentialsRegistry,
            tokenRegistry
    )

    val accountsRegistry = NitriteAccountsRegistry(db)
    val balanceService = BalanceService(accountsRegistry, "parking-common-stub")

    val statusService = object : StatusService {
        override fun status() = Status(true)
    }

    commonTokenRegistry = tokenRegistry

    val dataService = DataService(db)

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

            exception<Exception> {
                context.respond(HttpStatusCode.InternalServerError, ApplicationError.TechnicalError)
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

                        post("/get-balance") {
                            val request = call.receive<BalanceRequest>()
                            val response = balanceService.getBalance(request)
                            call.respond(response)
                        }
                    }
                }
            }

            route("/_s") {
                get("/get-data") {
                    val exporter = Exporter.of(db)
                    val writer = StringWriter()
                    exporter.exportTo(writer)
                    call.respondText(ContentType.Application.Json, HttpStatusCode.OK) {
                        writer.toString()
                    }
                }

                post("/post-data") {
                    val request = call.receive<DataRequest>()
                    dataService.doDataChange(request)
                    call.respond(HttpStatusCode.OK, "Ok")
                }
            }
        }
    }
    server.start(wait = true)
}
