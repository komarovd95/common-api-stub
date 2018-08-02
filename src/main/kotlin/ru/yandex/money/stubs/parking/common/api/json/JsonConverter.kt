package ru.yandex.money.stubs.parking.common.api.json

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.content.TextContent
import io.ktor.features.ContentConverter
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.pipeline.PipelineContext
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.readRemaining
import kotlinx.io.core.readText
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException
import java.io.Reader
import kotlin.reflect.KClass

class JsonConverter(private val types: Map<KClass<*>, (Reader) -> Any>) : ContentConverter {

    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val reader = channel.readRemaining()
                .readText(decoder = (context.call.request.contentCharset() ?: Charsets.UTF_8).newDecoder())
                .reader()
        val jsonFactory = types[request.type]
        val result = jsonFactory?.invoke(reader)
        if (result is ApplicationError) {
            throw ApplicationException(result)
        }
        return result
    }

    override suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>,
                                        contentType: ContentType,
                                        value: Any): Any? {
        if (value !is Json) {
            throw IllegalArgumentException("Unknown send type")
        }
        return TextContent(value.toJson(), contentType.withCharset(context.call.suitableCharset()))
    }
}
