package ru.yandex.money.stubs.parking.common.api.application.status

import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonAuthCommand
import ru.yandex.money.stubs.parking.common.api.service.status.StatusService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService

class GetStatusCommand(private val statusService: StatusService) : Command<Unit, StatusResponse> {
    override fun invoke(request: Unit) = StatusResponse(statusService.isActive())
}

fun getStatusCommand(statusService: StatusService, tokenService: TokenService) =
        CommonAuthCommand("get-status", GetStatusCommand(statusService), tokenService) {}
