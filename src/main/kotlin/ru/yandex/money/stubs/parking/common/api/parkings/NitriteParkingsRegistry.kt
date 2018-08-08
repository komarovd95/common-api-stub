package ru.yandex.money.stubs.parking.common.api.parkings

import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.data.DataException
import java.math.BigDecimal

class NitriteParkingsRegistry(private val db: Nitrite) : ParkingsRegistry {

    companion object {
        private val log = LoggerFactory.getLogger(NitriteParkingsRegistry::class.java)
    }

    init {
        val parkings = db.getCollection("parkings")
        if (!parkings.hasIndex("parkingId")) {
            parkings.createIndex("parkingId", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    override fun getParkingInfo(parkingId: Long): ParkingInfo {
        log.info("Fetching parking info: parkingId={}", parkingId)
        val parkings = db.getCollection("parkings")
        val cursor = parkings.find(Filters.eq("parkingId", parkingId))
        if (cursor.size() != 1) {
            log.warn("Failed to parking info by id: parkingId={}", parkingId)
            throw DataException("FIND_PARKING_INFO($parkingId)")
        }
        val document = cursor.single()
        val tariff = document["tariff", String::class.java]
        val parkingInfo = ParkingInfo(parkingId, BigDecimal(tariff))
        log.info("Fetching parking info finished: parkingInfo={}", parkingInfo)
        return parkingInfo
    }
}
