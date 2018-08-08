package ru.yandex.money.stubs.parking.common.api.commands.handlers

import io.ktor.application.ApplicationCall
import io.ktor.pipeline.PipelineInterceptor
import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.Request
import ru.yandex.money.stubs.parking.common.api.commands.Response

interface Handler {
    fun toInterceptor(): PipelineInterceptor<Unit, ApplicationCall>
}

fun handlerOf(command: Command<Request, Response>): PipelineInterceptor<Unit, ApplicationCall> =
        GuardedHandler(CommandHandler(command)).toInterceptor()
