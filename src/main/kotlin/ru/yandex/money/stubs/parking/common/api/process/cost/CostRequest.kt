package ru.yandex.money.stubs.parking.common.api.process.cost

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.contains
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import java.io.Reader
import java.time.Duration

class CostRequest(val accountNumber: String, val parkingId: String, val licensePlate: String, val duration: Duration) {

    companion object {
        val Deserializer: (Reader) -> Any = deserializer@{
            val jsonObject = JSONObject(it.readText())

            if ("licensePlate" !in jsonObject) {
                return@deserializer ApplicationError.InvalidLicensePlate
            }
            if ("parkingId" !in jsonObject) {
                return@deserializer ApplicationError.InvalidParkingId
            }
            if ("parkingAccountNumber" !in jsonObject) {
                return@deserializer ApplicationError.InvalidParkingAccountNumber
            }
            if ("duration" !in jsonObject) {
                return@deserializer ApplicationError.InvalidDuration
            }

            CostRequest(
                    accountNumber = jsonObject.getString("parkingAccountNumber"),
                    parkingId = jsonObject.getString("parkingId"),
                    licensePlate = jsonObject.getString("licensePlate"),
                    duration = Duration.parse(jsonObject.getString("duration"))
            )
        }
    }
}
