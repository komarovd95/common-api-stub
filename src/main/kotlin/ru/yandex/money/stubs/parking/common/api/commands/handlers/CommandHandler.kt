package ru.yandex.money.stubs.parking.common.api.commands.handlers

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineInterceptor
import io.ktor.response.contentType
import io.ktor.response.respond
import io.ktor.response.respondWrite
import io.ktor.util.toMap
import kotlinx.coroutines.experimental.io.readRemaining
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.Request
import ru.yandex.money.stubs.parking.common.api.commands.Response

class CommandHandler(private val command: Command<Request, Response>) : Handler {

    override fun toInterceptor(): PipelineInterceptor<Unit, ApplicationCall> = {
        val start = System.currentTimeMillis()
        log.info("Start executing command.")
        val headers = call.request.headers.toMap()
        val content = call.request.receiveChannel().readRemaining().readText()
        val request = Request(headers, content)
        val response = command(request)
        call.respondWrite(ContentType.parse(response.contentType), HttpStatusCode.fromValue(response.statusCode)) {
            write(response.data)
        }
        val end = System.currentTimeMillis()
        log.info("End execution of command: time={}ms", end - start)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CommandHandler::class.java)
    }
}