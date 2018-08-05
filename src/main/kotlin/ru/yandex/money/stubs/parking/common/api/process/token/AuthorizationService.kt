package ru.yandex.money.stubs.parking.common.api.process.token

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException
import java.time.ZonedDateTime
import java.util.*

class AuthorizationService(private val credentialsRegistry: CredentialsRegistry,
                           private val tokenRegistry: TokenRegistry) {

    companion object {
        private val log = LoggerFactory.getLogger(AuthorizationService::class.java)
    }

    fun createToken(credentials: TokenCredentials) = if (credentialsRegistry.resolve(credentials)) {
        val token = UUID.randomUUID().toString().replace("-", "")
        val expiresAt = ZonedDateTime.now().plusHours(1)
        val result = tokenRegistry.create(token, expiresAt)
        if (!result) {
            log.error("Failed to save token in registry.")
            throw ApplicationException(ApplicationError.TokenError)
        }
        log.info("Token saved successfully")
        Token(token, expiresAt)
    } else {
        log.warn("Token was not resolved: credentials={}", credentials)
        throw ApplicationException(ApplicationError.InvalidCredentials)
    }
}
