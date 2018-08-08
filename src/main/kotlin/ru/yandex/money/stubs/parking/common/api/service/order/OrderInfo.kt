package ru.yandex.money.stubs.parking.common.api.service.order

import ru.yandex.money.stubs.parking.common.api.gateways.orders.Order
import java.math.BigDecimal

data class OrderInfo(
    val orderId: String,
    val amountToPay: BigDecimal,
    val accountNumber: String,
    internal val order: Order
)
