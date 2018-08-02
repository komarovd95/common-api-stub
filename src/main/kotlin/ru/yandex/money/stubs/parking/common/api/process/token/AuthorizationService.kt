package ru.yandex.money.stubs.parking.common.api.process.token

import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException
import java.time.ZonedDateTime
import java.util.*

class AuthorizationService(private val credentialsRegistry: CredentialsRegistry,
                           private val tokenRegistry: TokenRegistry) {

    fun createToken(credentials: TokenCredentials) = if (credentialsRegistry.resolve(credentials)) {
        val token = UUID.randomUUID().toString().replace("-", "")
        val expiresAt = ZonedDateTime.now().plusHours(1)
        tokenRegistry.create(token, expiresAt)
        Token(token, expiresAt)
    } else {
        throw ApplicationException(ApplicationError.InvalidCredentials)
    }
}
