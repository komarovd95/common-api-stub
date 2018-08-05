package ru.yandex.money.stubs.parking.common.api.process.pay

import ru.yandex.money.stubs.parking.common.api.accounts.AccountsRegistry
import ru.yandex.money.stubs.parking.common.api.orders.OrdersRegistry

class PayService(private val ordersRegistry: OrdersRegistry, private val accountsRegistry: AccountsRegistry) {

    fun doPay(request: PayRequest) {
        val order = ordersRegistry.findOrder(request.orderId)
        val account = accountsRegistry.findAccount(order.accountNumber)
        
    }
}
