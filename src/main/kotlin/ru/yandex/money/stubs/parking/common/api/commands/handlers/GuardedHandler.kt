package ru.yandex.money.stubs.parking.common.api.commands.handlers

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.pipeline.PipelineInterceptor
import io.ktor.response.respondWrite
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.errors.ApplicationError
import ru.yandex.money.stubs.parking.common.api.commands.errors.AuthorizationError
import ru.yandex.money.stubs.parking.common.api.commands.errors.TechnicalError

class GuardedHandler(private val delegate: Handler) : Handler {

    override fun toInterceptor(): PipelineInterceptor<Unit, ApplicationCall> {
        val interceptor = delegate.toInterceptor()
        return {
            try {
                interceptor(this, Unit)
            } catch (ex: AuthorizationError) {
                log.error("Authorization error occurred while executing command.", ex)
                call.respondWrite(ContentType.Application.Json, HttpStatusCode.Unauthorized) {
                    write(ex.toJsonString())
                }
            } catch (ex: ApplicationError) {
                log.error("Error occurred while executing command.", ex)
                call.respondWrite(ContentType.Application.Json, HttpStatusCode.InternalServerError) {
                    write(ex.toJsonString())
                }
            } catch (ex: Exception) {
                log.error("Error occurred while executing command.", ex)
                call.respondWrite(ContentType.Application.Json, HttpStatusCode.InternalServerError) {
                    write(TechnicalError().toJsonString())
                }
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GuardedHandler::class.java)
    }
}
