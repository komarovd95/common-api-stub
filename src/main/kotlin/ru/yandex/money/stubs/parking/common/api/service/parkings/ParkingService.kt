package ru.yandex.money.stubs.parking.common.api.service.parkings

interface ParkingService {
    fun findParkingInfo(parkingId: Long): Parking
    fun findParkingInfos(parkingIds: Collection<Long>): Map<Long, Parking>
}
