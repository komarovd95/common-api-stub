package ru.yandex.money.stubs.parking.common.api.parkings

interface ParkingsRegistry {

    fun getParkingInfo(parkingId: Long): ParkingInfo
}
