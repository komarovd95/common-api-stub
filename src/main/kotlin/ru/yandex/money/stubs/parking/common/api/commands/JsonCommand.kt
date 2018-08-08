package ru.yandex.money.stubs.parking.common.api.commands

import io.ktor.http.ContentType
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.json.Json

class JsonCommand<RequestT, ResponseT : Json>(
        private val delegate: Command<RequestT, ResponseT>,
        private val jsonFactory: (String) -> RequestT
) : Command<Request, Response> {

    override fun invoke(request: Request): Response {
        val jsonRequest = jsonFactory(request.body)
        log.info("Json request: request={}", jsonRequest)
        val response = delegate(jsonRequest)
        log.info("Json response: response={}", response)
        return Response(200, ContentType.Application.Json.toString(), response.toJsonString())
    }

    companion object {
        private val log = LoggerFactory.getLogger(JsonCommand::class.java)
    }
}
