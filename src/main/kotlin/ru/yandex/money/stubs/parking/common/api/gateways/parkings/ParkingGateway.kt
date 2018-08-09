package ru.yandex.money.stubs.parking.common.api.gateways.parkings

import java.math.BigDecimal

interface ParkingGateway {
    fun findParkingInfo(parkingId: String): Triple<String, BigDecimal, String>
    fun findParkingInfos(parkingIds: Collection<String>): Map<String, Triple<String, BigDecimal, String>>
}
