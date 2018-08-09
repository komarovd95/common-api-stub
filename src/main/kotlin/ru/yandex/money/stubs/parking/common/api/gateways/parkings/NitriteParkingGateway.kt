package ru.yandex.money.stubs.parking.common.api.gateways.parkings

import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.NitriteGateway
import java.math.BigDecimal

class NitriteParkingGateway(db: Nitrite) :
    NitriteGateway(db, "parkings", listOf("parkingId")), ParkingGateway {

    override fun findParkingInfo(parkingId: String): Triple<String, BigDecimal, String> {
        val cursor = collection.find(Filters.eq(PARKING_ID_FIELD, parkingId))
        if (cursor.size() != 1) {
            throw GatewayException("find_parking_info($parkingId)")
        }
        val document = cursor.single()
        return Triple(
            parkingId,
            BigDecimal(document[TARIFF_FIELD, String::class.java]),
            document[PARKING_NAME_FIELD, String::class.java]
        )
    }

    override fun findParkingInfos(parkingIds: Collection<String>): Map<String, Triple<String, BigDecimal, String>> {
        val cursor = collection.find(Filters.`in`(PARKING_ID_FIELD, *parkingIds.toTypedArray()))
        val map = mutableMapOf<String, Triple<String, BigDecimal, String>>()
        cursor.map {
            val parkingId = it[PARKING_ID_FIELD, String::class.java]
            parkingId to Triple(parkingId, BigDecimal(it[TARIFF_FIELD, String::class.java]), it[PARKING_NAME_FIELD, String::class.java])
        }.toMap(map)
        return map.toMap()
    }

    companion object {
        private const val PARKING_ID_FIELD = "parkingId"
        private const val TARIFF_FIELD = "tariff"
        private const val PARKING_NAME_FIELD = "parkingName"
    }
}