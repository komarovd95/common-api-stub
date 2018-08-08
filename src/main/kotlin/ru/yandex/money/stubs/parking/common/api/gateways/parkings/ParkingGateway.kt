package ru.yandex.money.stubs.parking.common.api.gateways.parkings

import java.math.BigDecimal

interface ParkingGateway {
    fun findParkingInfo(parkingId: String): Pair<String, BigDecimal>
}
