package ru.yandex.money.stubs.parking.common.api.commands.factories

import ru.yandex.money.stubs.parking.common.api.commands.*
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService

class CommonAuthCommand<RequestT, ResponseT : Json>(
        commandName: String,
        command: Command<RequestT, ResponseT>,
        tokenService: TokenService,
        jsonFactory: (String) -> RequestT
) : Command<Request, Response> by AuthenticatedCommand(
        CommonCommand(
                commandName,
                command,
                jsonFactory
        ),
        tokenService
)
