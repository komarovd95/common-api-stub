package ru.yandex.money.stubs.parking.common.api.process.deposit

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.accounts.Account
import ru.yandex.money.stubs.parking.common.api.accounts.AccountsRegistry
import ru.yandex.money.stubs.parking.common.api.accounts.Balance
import ru.yandex.money.stubs.parking.common.api.data.DataException
import ru.yandex.money.stubs.parking.common.api.process.balance.BalanceRequest
import ru.yandex.money.stubs.parking.common.api.process.balance.BalanceResponse
import ru.yandex.money.stubs.parking.common.api.process.balance.BalanceService
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException
import java.math.BigDecimal

class DepositService(
        private val requestsRegistry: RequestsRegistry,
        private val accountsRegistry: AccountsRegistry,
        private val balanceService: BalanceService
) {

    companion object {
        private val log = LoggerFactory.getLogger(DepositService::class.java)
    }

    fun doDeposit(request: DepositRequest): BalanceResponse {
        if (request.requestId in requestsRegistry) {
            log.info("Deposit already done: request={}", request)
            return balanceService.getBalance(BalanceRequest(request.accountNumber))
        }

        val account = findAccount(request.accountNumber)
        val newAccount = account.copy(balance = Balance(account.balance.amount.add(request.money.amount)))

        if (!requestsRegistry.add(request.requestId)) {
            log.warn("Failed to insert request ID: request={}", request)
            throw ApplicationException(ApplicationError.TechnicalError)
        }

        accountsRegistry.saveAccount(newAccount)
        log.info("Deposit done successfully: account={}", newAccount)
        return balanceService.getBalance(BalanceRequest(request.accountNumber))
    }

    private fun findAccount(accountNumber: String) = try {
        accountsRegistry.findAccount(accountNumber)
    } catch (ex: DataException) {
        log.info("Failed to find account. Creating new one: accountNumber={}", accountNumber)
        Account(accountNumber, Balance(BigDecimal.ZERO))
    }
}
