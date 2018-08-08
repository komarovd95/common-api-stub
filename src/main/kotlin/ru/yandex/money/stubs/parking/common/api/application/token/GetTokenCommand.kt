package ru.yandex.money.stubs.parking.common.api.application.token

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonCommand
import ru.yandex.money.stubs.parking.common.api.service.token.Credentials
import ru.yandex.money.stubs.parking.common.api.service.token.TokenException
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService

class GetTokenCommand internal constructor(
        private val tokenService: TokenService
) : Command<GetTokenRequest, GetTokenResponse> {

    override fun invoke(request: GetTokenRequest): GetTokenResponse {
        try {
            val token = tokenService.createToken(Credentials(request.login, request.password))
            return GetTokenResponse(token.token, token.expiresAt)
        } catch (ex: TokenException) {
            log.error("Failed to create token.", ex)
            throw GetTokenError()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GetTokenCommand::class.java)
    }
}

fun getTokenCommand(tokenService: TokenService) = CommonCommand(
        "get-token",
        GetTokenCommand(tokenService)
) { GetTokenRequest(it) }
