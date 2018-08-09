package ru.yandex.money.stubs.parking.common.api.service.parkings

import java.math.BigDecimal

data class Parking(val parkingId: String, val tariff: BigDecimal, val parkingName: String)
