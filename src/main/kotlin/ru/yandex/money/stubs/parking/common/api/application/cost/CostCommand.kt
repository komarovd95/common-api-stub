package ru.yandex.money.stubs.parking.common.api.application.cost

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonAuthCommand
import ru.yandex.money.stubs.parking.common.api.commons.Balance
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountService
import ru.yandex.money.stubs.parking.common.api.service.order.CreatingOrder
import ru.yandex.money.stubs.parking.common.api.service.order.OrderService
import ru.yandex.money.stubs.parking.common.api.service.parkings.ParkingService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService
import java.math.BigDecimal
import java.time.Duration

class CostCommand internal constructor(
    private val accountService: AccountService,
    private val parkingService: ParkingService,
    private val orderService: OrderService
) : Command<CostRequest, CostResponse> {

    override fun invoke(request: CostRequest): CostResponse {
        val account = accountService.findAccount(request.parkingAccountNumber)
        log.info("Found account: account={}", account)
        val parking = parkingService.findParkingInfo(request.parkingId)
        log.info("Found parking info: parking={}", parking)

        val duration = request.duration
        val cost = parking.tariff.multiply(duration.toBigDecimal())
        log.info("Calculated cost: duration={}, cost={}", duration, cost)

        val order = orderService.storeOrder(
            CreatingOrder(
                parking = parking,
                account = account,
                licensePlate = request.licensePlate,
                duration = duration,
                cost = cost
            )
        )
        log.info("Order ID is: order={}", order)
        return CostResponse(order.orderId, Balance(order.amountToPay))
    }

    private fun Duration.toBigDecimal() = BigDecimal(this.toHours() + this.toMinutes() / 60.0)

    companion object {
        private val log = LoggerFactory.getLogger(CostCommand::class.java)
    }
}

fun costCommand(
    accountService: AccountService,
    parkingService: ParkingService,
    orderService: OrderService,
    tokenService: TokenService
) = CommonAuthCommand(
    "cost",
    CostCommand(accountService, parkingService, orderService),
    tokenService
) { CostRequest(it) }
