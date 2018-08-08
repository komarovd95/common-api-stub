package ru.yandex.money.stubs.parking.common.api.service.parkings

interface ParkingService {
    fun findParkingInfo(parkingId: Long): Parking
}
