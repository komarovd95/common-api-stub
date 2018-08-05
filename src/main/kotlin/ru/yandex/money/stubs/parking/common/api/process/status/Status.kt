package ru.yandex.money.stubs.parking.common.api.process.status

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

data class Status(private val status: Boolean) : Json {
    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val activeService = JSONObject()
        activeService["activeService"] = status

        jsonObject["status"] = activeService

        return jsonObject
    }
}
