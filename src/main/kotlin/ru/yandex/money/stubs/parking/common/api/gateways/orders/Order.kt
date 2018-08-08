package ru.yandex.money.stubs.parking.common.api.gateways.orders

import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime

data class Order(
    val orderId: String,
    val parkingId: String,
    val licensePlate: String,
    val accountNumber: String,
    val startTime: ZonedDateTime,
    val duration: Duration,
    val amount: BigDecimal,
    val paid: BigDecimal,
    val status: OrderStatus,
    val sessionReference: String
)
