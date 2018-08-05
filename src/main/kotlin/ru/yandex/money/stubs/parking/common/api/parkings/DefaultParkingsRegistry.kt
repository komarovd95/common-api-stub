package ru.yandex.money.stubs.parking.common.api.parkings

import ru.yandex.money.stubs.parking.common.api.data.DataException

class DefaultParkingsRegistry(
        private val delegate: ParkingsRegistry,
        private val default: ParkingInfo
) : ParkingsRegistry {

    override fun getParkingInfo(parkingId: Long) = try {
        delegate.getParkingInfo(parkingId)
    } catch (ex: DataException) {
        default
    }
}
