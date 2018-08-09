package ru.yandex.money.stubs.parking.common.api.service.token

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.credentials.CredentialsGateway
import ru.yandex.money.stubs.parking.common.api.gateways.tokens.TokenGateway
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*

class TokenService(
        private val credentialsGateway: CredentialsGateway,
        private val tokenGateway: TokenGateway,
        private val tokenFactory: () -> String = { UUID.randomUUID().toString().replace("-", "") },
        private val tokenLifetime: Duration = Duration.ofDays(365)
) {

    fun createToken(credentials: Credentials): Token {
        log.info("Resolving credentials.")
        if (!credentialsGateway.resolve(credentials.login, credentials.password)) {
            log.warn("Failed to resolve credentials: credentials={}", credentials)
            throw TokenException("Credentials are not resolved")
        }
        val token = tokenFactory()
        val expiresAt = ZonedDateTime.now().plus(tokenLifetime)
        val result = tokenGateway.createToken(token, expiresAt)
        if (!result) {
            log.error("Failed to create token.")
            throw TokenException("Unavailable to create token")
        }
        log.info("Token created successfully.")
        return Token(token, expiresAt)
    }

    fun isValidToken(token: String): Boolean {
        log.info("Checking if token is valid.")
        return try {
            val (_, expiresAt) = tokenGateway.findToken(token)
            expiresAt.isAfter(ZonedDateTime.now())
        } catch (ex: GatewayException) {
            log.error("Failed to check token to valid")
            false
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TokenService::class.java)
    }
}
