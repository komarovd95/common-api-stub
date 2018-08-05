package ru.yandex.money.stubs.parking.common.api.process.error

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

class ApplicationError(private val code: Int, private val message: String) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val error = JSONObject()
        error["code"] = code
        error["message"] = message

        jsonObject["error"] = error

        return jsonObject
    }

    companion object {
        val InvalidCredentials = ApplicationError(101, "Login or password is incorrect")
        val ExpiredToken = ApplicationError(102, "Token is expired")

        val ParkingAccountNotFound = ApplicationError(211, "Parking account not found")
        val InvalidParkingAccountNumber = ApplicationError(
            213,
            "Wrong parking account number format or parking account is not found"
        )

        val TokenError = ApplicationError(303, "Check of token is not possible")

        val TechnicalError = ApplicationError(500, "Technical Error")
    }
}
