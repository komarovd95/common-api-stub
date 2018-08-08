package ru.yandex.money.stubs.parking.common.api.service.accounts

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.accounts.AccountsGateway

class DirectAccountService(private val accountsGateway: AccountsGateway) : AccountService {

    override fun findAccount(accountNumber: String): Account {
        log.info("Finding account: accountNumber={}", accountNumber)
        try {
            val (_, balance) = accountsGateway.findAccount(accountNumber)
            return Account(accountNumber, balance)
        } catch (ex: GatewayException) {
            log.warn("Failed to find account: accountNumber={}", accountNumber)
            throw AccountsException("Failed to find account")
        }
    }

    override fun updateAccount(account: Account): Boolean {
        log.info("Updating account: account={}", account)
        return accountsGateway.updateAccount(account.accountNumber, account.balance)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DirectAccountService::class.java)
    }
}
