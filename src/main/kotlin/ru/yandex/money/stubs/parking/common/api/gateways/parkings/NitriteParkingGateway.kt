package ru.yandex.money.stubs.parking.common.api.gateways.parkings

import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.NitriteGateway
import java.math.BigDecimal

class NitriteParkingGateway(db: Nitrite) :
    NitriteGateway(db, "parkings", listOf("parkingId")), ParkingGateway {

    override fun findParkingInfo(parkingId: String): Pair<String, BigDecimal> {
        val cursor = collection.find(Filters.eq(PARKING_ID_FIELD, parkingId))
        if (cursor.size() != 1) {
            throw GatewayException("find_parking_info($parkingId)")
        }
        val document = cursor.single()
        return parkingId to BigDecimal(document[TARIFF_FIELD, String::class.java])
    }

    companion object {
        private const val PARKING_ID_FIELD = "parkingId"
        private const val TARIFF_FIELD = "tariff"
    }
}