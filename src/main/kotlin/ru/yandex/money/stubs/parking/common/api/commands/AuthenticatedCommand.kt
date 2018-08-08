package ru.yandex.money.stubs.parking.common.api.commands

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.errors.AuthorizationError
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService

class AuthenticatedCommand<ResponseT>(
        private val delegate: Command<Request, ResponseT>,
        private val tokenService: TokenService
) : Command<Request, ResponseT> {

    override fun invoke(request: Request): ResponseT {
        log.info("Resolving token.")
        val headers = request.headers
        if (AUTHORIZATION_HEADER !in headers) {
            log.warn("No authorization header in request: headers={}, headerName={}", headers, AUTHORIZATION_HEADER)
            throw AuthorizationError.ExpiredToken
        }
        val values = headers[AUTHORIZATION_HEADER]!!
        if (values.isEmpty()) {
            log.warn("No authorization token found.")
            throw AuthorizationError.ExpiredToken
        }
        val token = values[0]
        if (!token.matches(PATTERN.toRegex())) {
            log.warn("Authorization token has illegal format: token={}", token)
            throw AuthorizationError.ExpiredToken
        }
        if (!tokenService.isValidToken(token)) {
            log.warn("Failed to find token in token registry: token={}", token)
            throw AuthorizationError.ExpiredToken
        }
        log.info("Successfully resolved token.")
        return delegate(request)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthenticatedCommand::class.java)

        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val PATTERN = "^([a-fA-F0-9]{32})$"
    }
}