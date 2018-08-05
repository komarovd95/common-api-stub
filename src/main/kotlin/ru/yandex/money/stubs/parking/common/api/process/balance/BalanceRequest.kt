package ru.yandex.money.stubs.parking.common.api.process.balance

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import java.io.Reader

data class BalanceRequest(val parkingAccountNumber: String) {

    companion object {

        private const val PARKING_ACCOUNT_NUMBER = "parkingAccountNumber"

        val Deserializer: (Reader) -> Any = {
            val jsonObject = JSONObject(it.readText())

            if (jsonObject.has(PARKING_ACCOUNT_NUMBER)) {
                BalanceRequest(jsonObject.getString(PARKING_ACCOUNT_NUMBER))
            } else {
                ApplicationError.InvalidParkingAccountNumber
            }
        }
    }
}