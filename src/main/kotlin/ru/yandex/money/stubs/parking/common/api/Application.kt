package ru.yandex.money.stubs.parking.common.api

import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.ContentType
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.dizitart.no2.Document
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import ru.yandex.money.stubs.parking.common.api.application.balance.getBalanceCommand
import ru.yandex.money.stubs.parking.common.api.application.cost.costCommand
import ru.yandex.money.stubs.parking.common.api.application.deposit.depositCommand
import ru.yandex.money.stubs.parking.common.api.application.pay.payCommand
import ru.yandex.money.stubs.parking.common.api.application.status.getStatusCommand
import ru.yandex.money.stubs.parking.common.api.application.stop.stopParkingCommand
import ru.yandex.money.stubs.parking.common.api.application.token.getTokenCommand
import ru.yandex.money.stubs.parking.common.api.commands.handlers.handlerOf
import ru.yandex.money.stubs.parking.common.api.config.ApplicationConfig
import ru.yandex.money.stubs.parking.common.api.data.change.changeDataCommand
import ru.yandex.money.stubs.parking.common.api.data.get.getDataCommand
import ru.yandex.money.stubs.parking.common.api.gateways.accounts.NitriteAccountsGateway
import ru.yandex.money.stubs.parking.common.api.gateways.credentials.NitriteCredentialsGateway
import ru.yandex.money.stubs.parking.common.api.gateways.orders.NitriteOrderGateway
import ru.yandex.money.stubs.parking.common.api.gateways.parkings.NitriteParkingGateway
import ru.yandex.money.stubs.parking.common.api.gateways.tokens.NitriteTokenGateway
import ru.yandex.money.stubs.parking.common.api.service.accounts.CreateNewAccountService
import ru.yandex.money.stubs.parking.common.api.service.accounts.DirectAccountService
import ru.yandex.money.stubs.parking.common.api.service.data.NitriteDataService
import ru.yandex.money.stubs.parking.common.api.service.deposit.DepositService
import ru.yandex.money.stubs.parking.common.api.service.order.OrderService
import ru.yandex.money.stubs.parking.common.api.service.parkings.DefaultParkingService
import ru.yandex.money.stubs.parking.common.api.service.parkings.DirectParkingService
import ru.yandex.money.stubs.parking.common.api.service.pay.PayService
import ru.yandex.money.stubs.parking.common.api.service.status.DummyStatusService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Properties

fun main(args: Array<String>) {
    val config = if (args.isEmpty()) {
        ApplicationConfig.DefaultConig
    } else {
        ApplicationConfig(Properties().also { it.load(Files.newInputStream(Paths.get(args[0]))) })
    }

    val db = Nitrite.builder()
        .compressed()
        .filePath(config.databasePath)
        .openOrCreate()

    val credentialsGateway = NitriteCredentialsGateway(db) {
        if (it.find(Filters.eq(NitriteCredentialsGateway.LOGIN_FIELD, config.defaultLogin)).size() == 0) {
            val credentials = Document.createDocument(NitriteCredentialsGateway.LOGIN_FIELD, config.defaultLogin)
                .put(NitriteCredentialsGateway.PASSWORD_FIELD, config.defaultPassword)
            it.update(credentials, true)
        }
    }
    val tokenGateway = NitriteTokenGateway(db)
    val accountsGateway = NitriteAccountsGateway(db)
    val parkingGateway = NitriteParkingGateway(db)
    val orderGateway = NitriteOrderGateway(db)

    val tokenService = TokenService(credentialsGateway, tokenGateway)
    val statusService = DummyStatusService()
    val accountService = DirectAccountService(accountsGateway)
    val createNewAccountService = CreateNewAccountService(accountService, accountsGateway)
    val depositService = DepositService()
    val parkingService = DefaultParkingService(DirectParkingService(parkingGateway))
    val orderService = OrderService(orderGateway, parkingService, accountService)
    val payService = PayService(accountService, orderService)
    val dataService = NitriteDataService(db)

    val server = embeddedServer(Netty, host = config.host, port = config.port.toInt()) {
        install(DefaultHeaders)
        install(CallLogging)

        routing {
            accept(ContentType.Application.Json) {
                post("/token", handlerOf(getTokenCommand(tokenService)))
                route("/v1") {
                    post("/token", handlerOf(getTokenCommand(tokenService)))
                    post("/status", handlerOf(getStatusCommand(statusService, tokenService)))
                    post("/get-balance", handlerOf(getBalanceCommand(createNewAccountService, tokenService)))
                    post("/deposit", handlerOf(depositCommand(depositService, accountService, tokenService)))
                    post("/cost", handlerOf(costCommand(createNewAccountService, parkingService, orderService, tokenService)))
                    post("/pay", handlerOf(payCommand(orderService, accountService, payService, tokenService)))
                    post("/stop-parking", handlerOf(stopParkingCommand(orderService, tokenService)))
                }
                route("/_s") {
                    get("/get-data", handlerOf(getDataCommand(dataService)))
                    post("/post-data", handlerOf(changeDataCommand(dataService)))
                }
            }
        }
    }
    server.start(wait = true)
}
