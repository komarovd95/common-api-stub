package ru.yandex.money.stubs.parking.common.api.commands.factories

import ru.yandex.money.stubs.parking.common.api.commands.*
import ru.yandex.money.stubs.parking.common.api.json.Json

class CommonCommand<RequestT, ResponseT : Json>(
        commandName: String,
        command: Command<RequestT, ResponseT>,
        jsonFactory: (String) -> RequestT
) : Command<Request, Response> by LoggingCommand(
        JsonCommand(command, jsonFactory),
        commandName
)