package ru.yandex.money.stubs.parking.common.api.application.pay

import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonAuthCommand
import ru.yandex.money.stubs.parking.common.api.service.pay.PayService
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountService
import ru.yandex.money.stubs.parking.common.api.service.order.OrderService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService
import java.time.ZonedDateTime

class PayCommand internal constructor(
    private val orderService: OrderService,
    private val accountService: AccountService,
    private val payService: PayService
) : Command<PayRequest, PayResponse> {

    override fun invoke(request: PayRequest): PayResponse {
        val order = orderService.findOrder(request.orderId)
        val account = accountService.findAccount(order.accountNumber)
        val sessionInfo = payService.doPay(account, order)
        return PayResponse(
            sessionId = sessionInfo.sessionId,
            serverTime = ZonedDateTime.now(),
            startTime = sessionInfo.startTime,
            endTime = sessionInfo.endTime
        )
    }
}

fun payCommand(
    orderService: OrderService,
    accountService: AccountService,
    payService: PayService,
    tokenService: TokenService
) = CommonAuthCommand("pay", PayCommand(orderService, accountService, payService), tokenService) { PayRequest(it) }
