package ru.yandex.money.stubs.parking.common.api.service.parkings

import org.slf4j.LoggerFactory
import java.math.BigDecimal

class DefaultParkingService(
    private val parkingService: ParkingService,
    private val defaultTariff: BigDecimal = BigDecimal("60.00")
) : ParkingService {

    override fun findParkingInfo(parkingId: Long) = try {
        parkingService.findParkingInfo(parkingId)
    } catch (ex: ParkingException) {
        log.warn("Parking info not found. Return default one.")
        Parking(parkingId.toString(), defaultTariff, "TestParking")
    }

    override fun findParkingInfos(parkingIds: Collection<Long>): Map<Long, Parking> {
        val map = mutableMapOf<Long, Parking>()
        parkingIds.forEachIndexed { index, id ->
            map[id] = Parking(id.toString(), defaultTariff, "TestParking$index")
        }
        return map.toMap()
    }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultParkingService::class.java)
    }
}