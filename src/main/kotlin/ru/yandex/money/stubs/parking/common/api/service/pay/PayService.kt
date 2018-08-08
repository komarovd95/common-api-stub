package ru.yandex.money.stubs.parking.common.api.service.pay

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.service.accounts.Account
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountService
import ru.yandex.money.stubs.parking.common.api.service.order.OrderInfo
import ru.yandex.money.stubs.parking.common.api.service.order.OrderService
import ru.yandex.money.stubs.parking.common.api.service.order.SessionInfo

class PayService(private val accountService: AccountService, private val orderService: OrderService) {

    fun doPay(account: Account, order: OrderInfo): SessionInfo {
        log.info("Doing pay: account={}, order={}", account, order)
        if (account.balance < order.amountToPay) {
            log.warn("Balance is less than amount to pay: account={}, order={}", account, order)
            throw PayException("Balance error")
        }

        val updatedAccount = account.copy(balance = account.balance - order.amountToPay)
        val updated = accountService.updateAccount(updatedAccount)
        if (!updated) {
            log.warn("Failed to update account's balance: account={}", updatedAccount)
            throw PayException("Update error")
        }
        return orderService.payForOrder(order)
    }

    companion object {
        private val log = LoggerFactory.getLogger(PayService::class.java)
    }
}
