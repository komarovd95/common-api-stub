package ru.yandex.money.stubs.parking.common.api.process.error

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

class ApplicationError private constructor(private val code: Int, private val message: String) : Json {

    override fun toJson(): String {
        val jsonObject = JSONObject()

        val error = JSONObject()
        error["code"] = code
        error["message"] = message

        jsonObject["error"] = error

        return jsonObject.toString()
    }

    companion object {
        val InvalidCredentials = ApplicationError(101, "Login or password is incorrect")
        val ExpiredToken = ApplicationError(102, "Token is expired")
    }
}
