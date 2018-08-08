package ru.yandex.money.stubs.parking.common.api.application.stop

import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonAuthCommand
import ru.yandex.money.stubs.parking.common.api.commons.Balance
import ru.yandex.money.stubs.parking.common.api.service.order.OrderService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService
import java.time.ZonedDateTime

class StopParkingCommand internal constructor(
    private val orderService: OrderService
) : Command<StopParkingRequest, StopParkingResponse> {

    override fun invoke(request: StopParkingRequest): StopParkingResponse {
        val (sessionInfo, refund) = orderService.stopSession(request.sessionId)
        return StopParkingResponse(
            sessionId = request.sessionId,
            serverTime = ZonedDateTime.now(),
            startTime = sessionInfo.startTime,
            endTime = sessionInfo.endTime,
            refund = Balance(refund)
        )
    }
}


fun stopParkingCommand(orderService: OrderService, tokenService: TokenService) = CommonAuthCommand(
    "stop-parking",
    StopParkingCommand(orderService),
    tokenService
) { StopParkingRequest(it) }
