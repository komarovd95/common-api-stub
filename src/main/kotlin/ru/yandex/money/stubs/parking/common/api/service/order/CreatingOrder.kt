package ru.yandex.money.stubs.parking.common.api.service.order

import ru.yandex.money.stubs.parking.common.api.service.accounts.Account
import ru.yandex.money.stubs.parking.common.api.service.parkings.Parking
import java.math.BigDecimal
import java.time.Duration

data class CreatingOrder(
    val parking: Parking,
    val account: Account,
    val licensePlate: String,
    val duration: Duration,
    val cost: BigDecimal
)
