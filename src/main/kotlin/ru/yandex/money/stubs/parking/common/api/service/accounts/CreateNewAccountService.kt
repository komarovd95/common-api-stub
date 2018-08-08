package ru.yandex.money.stubs.parking.common.api.service.accounts

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.gateways.accounts.AccountsGateway
import java.math.BigDecimal

class CreateNewAccountService(private val accountService: AccountService,
                              private val accountsGateway: AccountsGateway) : AccountService by accountService {

    override fun findAccount(accountNumber: String) = try {
        accountService.findAccount(accountNumber)
    } catch (ex: AccountsException) {
        log.info("Account not found. Creating new one.")
        if (!accountsGateway.createAccount(accountNumber)) {
            log.warn("Failed to create account: accountNumber={}", accountNumber)
            throw AccountsException("create_account_after_fail($accountNumber)")
        }
        Account(accountNumber, BigDecimal.ZERO)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CreateNewAccountService::class.java)
    }
}
