package ru.yandex.money.stubs.parking.common.api.application.token

import ru.yandex.money.stubs.parking.common.api.commands.errors.ApplicationError

class GetTokenError : ApplicationError(303, "Check of token is not possible")
