package ru.yandex.money.stubs.parking.common.api.service.parkings

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.parkings.ParkingGateway

class DirectParkingService(private val parkingGateway: ParkingGateway) : ParkingService {

    override fun findParkingInfo(parkingId: Long): Parking {
        log.info("Finding parking info: parkingId={}", parkingId)
        return try {
            val (id, tariff, name) = parkingGateway.findParkingInfo(parkingId.toString())
            Parking(id, tariff, name)
        } catch (ex: GatewayException) {
            log.info("Failed to find parking info in gateway: parkingId={}", parkingId)
            throw ParkingException("Failed to find parking info")
        }
    }

    override fun findParkingInfos(parkingIds: Collection<Long>): Map<Long, Parking> {
        log.info("Finding parking infos: parkingIds={}", parkingIds)
        return parkingGateway.findParkingInfos(parkingIds.map { it.toString() })
            .mapKeys { (k, _) ->
                k.toLong()
            }
            .mapValues { (_, v) ->
                Parking(v.first, v.second, v.third)
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DirectParkingService::class.java)
    }
}
