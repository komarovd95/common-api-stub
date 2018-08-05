package ru.yandex.money.stubs.parking.common.api.process.balance

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import java.io.Reader

data class BalanceRequest(val parkingAccountNumber: String) {

    companion object {

        private val PARKING_ID_PATTERN = "^[0-9]{10}$".toRegex()

        private const val PARKING_ACCOUNT_NUMBER = "parkingAccountNumber"

        val Deserializer: (Reader) -> Any = {
            val jsonObject = JSONObject(it.readText())

            if (jsonObject.has(PARKING_ACCOUNT_NUMBER)) {
                val parkingAccountNumber = jsonObject.getString(PARKING_ACCOUNT_NUMBER)

                if (!parkingAccountNumber.matches(PARKING_ID_PATTERN)) {
                    ApplicationError.InvalidParkingAccountNumber
                } else {

                    BalanceRequest(parkingAccountNumber)
                }
            } else {
                ApplicationError.InvalidParkingAccountNumber
            }
        }
    }
}