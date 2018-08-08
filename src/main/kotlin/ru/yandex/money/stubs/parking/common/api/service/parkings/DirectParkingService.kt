package ru.yandex.money.stubs.parking.common.api.service.parkings

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.parkings.ParkingGateway

class DirectParkingService(private val parkingGateway: ParkingGateway) : ParkingService {

    override fun findParkingInfo(parkingId: Long): Parking {
        log.info("Finding parking info: parkingId={}", parkingId)
        return try {
            val (id, tariff) = parkingGateway.findParkingInfo(parkingId.toString())
            Parking(id, tariff)
        } catch (ex: GatewayException) {
            log.info("Failed to find parking info in gateway: parkingId={}", parkingId)
            throw ParkingException("Failed to find parking info")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DirectParkingService::class.java)
    }
}
