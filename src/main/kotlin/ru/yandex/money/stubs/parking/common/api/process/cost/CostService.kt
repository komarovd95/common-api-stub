package ru.yandex.money.stubs.parking.common.api.process.cost

import ru.yandex.money.stubs.parking.common.api.accounts.Account
import ru.yandex.money.stubs.parking.common.api.accounts.AccountsRegistry
import ru.yandex.money.stubs.parking.common.api.accounts.Balance
import ru.yandex.money.stubs.parking.common.api.data.DataException
import ru.yandex.money.stubs.parking.common.api.orders.Order
import ru.yandex.money.stubs.parking.common.api.orders.OrdersRegistry
import ru.yandex.money.stubs.parking.common.api.parkings.ParkingsRegistry
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

class CostService(
        private val accountsRegistry: AccountsRegistry,
        private val parkingsRegistry: ParkingsRegistry,
        private val ordersRegistry: OrdersRegistry
) {

    fun getCost(request: CostRequest): OrderDetails {
        val account = findAccount(request.accountNumber)

        val parkingInfo = parkingsRegistry.getParkingInfo(request.parkingId.toLong())

        val duration = request.duration
        val cost = parkingInfo.tariff.multiply(BigDecimal(duration.toHours() + (duration.toMinutes() / 60.0)))

        val order = Order(
                orderId = UUID.randomUUID().toString(),
                parkingId = parkingInfo.parkingId,
                accountNumber = account.accountNumber,
                createdAt = ZonedDateTime.now(),
                duration = duration,
                amount = cost
        )
        ordersRegistry.createOrder(order)

        return OrderDetails(order)
    }

    private fun findAccount(accountNumber: String) = try {
        accountsRegistry.findAccount(accountNumber)
    } catch (ex: DataException) {
        val account = Account(accountNumber, Balance(BigDecimal.ZERO))
        accountsRegistry.saveAccount(account)
        account
    }
}
