package ru.yandex.money.stubs.parking.common.api.commands

interface Command<RequestT, ResponseT> {
    operator fun invoke(request: RequestT): ResponseT
}
