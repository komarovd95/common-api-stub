package ru.yandex.money.stubs.parking.common.api.application.balance

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.errors.TechnicalError
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonAuthCommand
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountService
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountsException
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService

class GetBalanceCommand internal constructor(
        private val accountService: AccountService,
        private val operatorName: String = "parking-stub"
) : Command<GetBalanceRequest, GetBalanceResponse> {

    override fun invoke(request: GetBalanceRequest): GetBalanceResponse {
        try {
            val account = accountService.findAccount(request.parkingAccountNumber)
            return GetBalanceResponse(
                    Balance(account.balance),
                    operatorName
            )
        } catch (ex: AccountsException) {
            log.warn("Failed to get balance.", ex)
            throw TechnicalError()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GetBalanceCommand::class.java)
    }
}

fun getBalanceCommand(accountService: AccountService, tokenService: TokenService) = CommonAuthCommand(
        "get-balance",
        GetBalanceCommand(accountService),
        tokenService
) { GetBalanceRequest(it) }
