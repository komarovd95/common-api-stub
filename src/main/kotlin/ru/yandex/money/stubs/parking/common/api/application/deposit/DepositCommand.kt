package ru.yandex.money.stubs.parking.common.api.application.deposit

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.commands.Command
import ru.yandex.money.stubs.parking.common.api.commands.errors.TechnicalError
import ru.yandex.money.stubs.parking.common.api.commands.factories.CommonAuthCommand
import ru.yandex.money.stubs.parking.common.api.commons.Balance
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountService
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountsException
import ru.yandex.money.stubs.parking.common.api.service.deposit.DepositService
import ru.yandex.money.stubs.parking.common.api.service.token.TokenService

class DepositCommand internal constructor(
        private val depositService: DepositService,
        private val accountService: AccountService,
        private val operatorName: String = "parking-stub"
) : Command<DepositRequest, DepositResponse> {

    override fun invoke(request: DepositRequest): DepositResponse {
        if (depositService.isDepositDone(request.requestId)) {
            log.info("Deposit already done: requestId={}", request.requestId)
            try {
                val account = accountService.findAccount(request.parkingAccountNumber)
                return DepositResponse(
                        Balance(account.balance),
                        operatorName
                )
            } catch (ex: AccountsException) {
                log.error("Failed to find account: accountNumber={}", request.parkingAccountNumber)
                throw TechnicalError()
            }
        }

        try {
            val account = accountService.findAccount(request.parkingAccountNumber)
            val depositedAccount = account.copy(balance = account.balance + request.money.amount)
            val updated = accountService.updateAccount(depositedAccount)
            if (!updated) {
                log.warn("Failed to update account balance: account={}", depositedAccount)
                throw TechnicalError()
            }
            depositService.depositDone(request.requestId)
            return DepositResponse(
                    Balance(depositedAccount.balance),
                    operatorName
            )
        } catch (ex: AccountsException) {
            log.warn("Failed to find account: accountNumber={}", request.parkingAccountNumber)
            throw TechnicalError()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DepositCommand::class.java)
    }
}

fun depositCommand(depositService: DepositService,
                   accountService: AccountService,
                   tokenService: TokenService) =
        CommonAuthCommand(
                "deposit",
                DepositCommand(depositService, accountService),
                tokenService
        ) { DepositRequest(it) }
