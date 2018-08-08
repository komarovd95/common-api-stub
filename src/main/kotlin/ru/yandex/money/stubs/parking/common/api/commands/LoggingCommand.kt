package ru.yandex.money.stubs.parking.common.api.commands

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

class LoggingCommand<RequestT, ResponseT>(
        private val delegate: Command<RequestT, ResponseT>,
        private val commandName: String
) : Command<RequestT, ResponseT> {

    override fun invoke(request: RequestT): ResponseT {
        MDC.put("command_name", commandName)
        MDC.put("trace_id", UUID.randomUUID().toString())
        log.info("Received request: request={}", request)
        val response = delegate(request)
        log.info("Response: response={}", response)
        MDC.clear()
        return response
    }

    companion object {
        private val log = LoggerFactory.getLogger(LoggingCommand::class.java)
    }
}
