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
import ru.yandex.money.stubs.parking.common.api.application.balance.getBalanceCommand
import ru.yandex.money.stubs.parking.common.api.application.deposit.depositCommand
import ru.yandex.money.stubs.parking.common.api.application.status.getStatusCommand
import ru.yandex.money.stubs.parking.common.api.application.token.getTokenCommand
import ru.yandex.money.stubs.parking.common.api.auth.AuthorizationException
import ru.yandex.money.stubs.parking.common.api.auth.authenticated
import ru.yandex.money.stubs.parking.common.api.auth.commonTokenRegistry
import ru.yandex.money.stubs.parking.common.api.commands.handlers.handlerOf
import ru.yandex.money.stubs.parking.common.api.data.DataRequest
import ru.yandex.money.stubs.parking.common.api.data.DataService
import ru.yandex.money.stubs.parking.common.api.gateways.accounts.NitriteAccountsGateway
import ru.yandex.money.stubs.parking.common.api.gateways.credentials.NitriteCredentialsGateway
import ru.yandex.money.stubs.parking.common.api.gateways.tokens.NitriteTokenGateway
import ru.yandex.money.stubs.parking.common.api.json.JsonConverter
import ru.yandex.money.stubs.parking.common.api.orders.NitriteOrdersRegistry
import ru.yandex.money.stubs.parking.common.api.parkings.DefaultParkingsRegistry
import ru.yandex.money.stubs.parking.common.api.parkings.NitriteParkingsRegistry
import ru.yandex.money.stubs.parking.common.api.parkings.ParkingInfo
import ru.yandex.money.stubs.parking.common.api.process.balance.BalanceRequest
import ru.yandex.money.stubs.parking.common.api.process.balance.BalanceService
import ru.yandex.money.stubs.parking.common.api.process.cost.CostRequest
import ru.yandex.money.stubs.parking.common.api.process.cost.CostService
import ru.yandex.money.stubs.parking.common.api.process.deposit.DepositRequest
import ru.yandex.money.stubs.parking.common.api.process.deposit.NitriteRequestsRegistry
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException
import ru.yandex.money.stubs.parking.common.api.process.pay.PayService
import ru.yandex.money.stubs.parking.common.api.process.status.Status
import ru.yandex.money.stubs.parking.common.api.process.status.StatusService
import ru.yandex.money.stubs.parking.common.api.process.token.AuthorizationService
import ru.yandex.money.stubs.parking.common.api.process.token.NitriteCredentialsRegistry
import ru.yandex.money.stubs.parking.common.api.process.token.NitriteTokenRegistry
import ru.yandex.money.stubs.parking.common.api.process.token.TokenCredentials
import ru.yandex.money.stubs.parking.common.api.service.accounts.CreateNewAccountService
import ru.yandex.money.stubs.parking.common.api.service.accounts.DirectAccountService
import ru.yandex.money.stubs.parking.common.api.service.deposit.DepositService
import ru.yandex.money.stubs.parking.common.api.service.status.DummyStatusService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService
import java.io.StringWriter
import java.math.BigDecimal

fun main(args: Array<String>) {
    val jsonConverter = JsonConverter(
            mapOf(
                TokenCredentials::class to TokenCredentials.Deserializer,
                BalanceRequest::class to BalanceRequest.Deserializer,
                DataRequest::class to DataRequest.Deserializer,
                DepositRequest::class to DepositRequest.Deserializer,
                CostRequest::class to CostRequest.Deserializer
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

    val requestsRegistry = NitriteRequestsRegistry(db)
//    val depositService = DepositService(requestsRegistry, accountsRegistry, balanceService)

    val parkingsRegistry = DefaultParkingsRegistry(
            NitriteParkingsRegistry(db),
            ParkingInfo(1001, BigDecimal("60.00"))
    )
    val ordersRegistry = NitriteOrdersRegistry(db)

    val costService = CostService(accountsRegistry, parkingsRegistry, ordersRegistry)

//    val payService = PayService(ordersRegistry)

//    val statusService = object : StatusService {
//        override fun status() = Status(true)
//    }

    commonTokenRegistry = tokenRegistry

    val dataService = DataService(db)







    val credentialsGateway = NitriteCredentialsGateway(db) {
        if (it.find(Filters.eq(NitriteCredentialsGateway.LOGIN_FIELD, "yandex")).size() == 1) {
            return@NitriteCredentialsGateway
        }

        val credentials = Document.createDocument(NitriteCredentialsGateway.LOGIN_FIELD, "yandex")
                .put(NitriteCredentialsGateway.PASSWORD_FIELD, "yandex")
        it.update(credentials, true)
    }
    val tokenGateway = NitriteTokenGateway(db)
    val accountsGateway = NitriteAccountsGateway(db)

    val tokenService = TokenService(credentialsGateway, tokenGateway)
    val statusService = DummyStatusService()
    val accountService = DirectAccountService(accountsGateway)
    val createNewAccountService = CreateNewAccountService(accountService, accountsGateway)
    val depositService = DepositService()

    val server = embeddedServer(Netty, port = 8080) {
        install(DefaultHeaders)
        install(CallLogging)
//        install(ContentNegotiation) {
//            register(ContentType.Application.Json, jsonConverter)
//        }

//        install(StatusPages) {
//            exception<ApplicationException> {
//                context.respond(HttpStatusCode.BadRequest, it.error)
//            }
//
//            exception<AuthorizationException> {
//                context.respond(HttpStatusCode.Unauthorized, ApplicationError.ExpiredToken)
//            }
//
//            exception<Exception> {
//                context.respond(HttpStatusCode.InternalServerError, ApplicationError.TechnicalError)
//            }
//        }

        routing {
            accept(ContentType.Application.Json) {
                route("/v1") {
                    post("/token", handlerOf(getTokenCommand(tokenService)))

                    post("/status", handlerOf(getStatusCommand(statusService, tokenService)))

                    post("/get-balance", handlerOf(getBalanceCommand(createNewAccountService, tokenService)))

                    post("/deposit", handlerOf(depositCommand(depositService, accountService, tokenService)))

//                    {
//                        val credentials = call.receive<TokenCredentials>()
//                        val token = authorizationService.createToken(credentials)
//                        call.respond(token)
//                    }

//                    authenticated {
////                        post("/status") {
////                            call.respond(statusService.status())
////                        }
//
//                        post("/get-balance") {
//                            val request = call.receive<BalanceRequest>()
//                            val response = balanceService.getBalance(request)
//                            call.respond(response)
//                        }
//
//                        post("/deposit") {
//                            val request = call.receive<DepositRequest>()
//                            val response = depositService.doDeposit(request)
//                            call.respond(response)
//                        }
//
//                        post("/cost") {
//                            val request = call.receive<CostRequest>()
//                            val details = costService.getCost(request)
//                            call.respond(details)
//                        }
//                    }
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
