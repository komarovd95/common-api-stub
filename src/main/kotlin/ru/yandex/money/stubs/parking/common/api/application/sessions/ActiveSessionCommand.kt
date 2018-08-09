package ru.yandex.money.stubs.parking.common.api.application.sessions

import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonAuthCommand
import ru.yandex.money.stubs.parking.common.api.service.order.OrderService
import ru.yandex.money.stubs.parking.common.api.service.parkings.ParkingService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService
import java.time.ZonedDateTime

class ActiveSessionCommand internal constructor(
    private val orderService: OrderService,
    private val parkingService: ParkingService
) : Command<ActiveSessionRequest, ActiveSessionResponse> {

    override fun invoke(request: ActiveSessionRequest): ActiveSessionResponse {
        val orders = orderService.findOrders(request.parkingAccountNumber, request.licensePlate)
        val parkingIds = orders.map { it.parkingId.toLong() }
        val parkingInfos = parkingService.findParkingInfos(parkingIds)
        return ActiveSessionResponse(
            ZonedDateTime.now(),
            orders.map {
                ActiveSession(
                    sessionId = it.sessionReference,
                    parkingId = it.parkingId,
                    licensePlate = it.licensePlate,
                    parkingName = parkingInfos[it.parkingId.toLong()]!!.parkingName,
                    startTime = it.startTime,
                    endTime = it.startTime.plus(it.duration)
                )
            }
        )
    }
}

fun activeSessionsCommand(orderService: OrderService, parkingService: ParkingService, tokenService: TokenService) =
    CommonAuthCommand(
        "active-sessions",
        ActiveSessionCommand(
            orderService,
            parkingService
        ),
        tokenService
    ) { ActiveSessionRequest(it) }
