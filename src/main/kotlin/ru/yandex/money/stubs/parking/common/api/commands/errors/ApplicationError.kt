package ru.yandex.money.stubs.parking.common.api.commands.errors

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

abstract class ApplicationError(
        private val errorCode: Int,
        private val errorMessage: String
) : Exception(errorMessage), Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val error = JSONObject()
        error["code"] = errorCode
        error["message"] = errorMessage

        jsonObject["error"] = error

        return jsonObject
    }
}
