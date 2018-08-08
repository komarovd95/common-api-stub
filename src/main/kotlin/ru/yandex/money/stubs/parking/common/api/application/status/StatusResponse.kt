package ru.yandex.money.stubs.parking.common.api.application.status

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

data class StatusResponse(private val active: Boolean) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val activeService = JSONObject()
        activeService["activeService"] = active

        jsonObject["status"] = activeService

        return jsonObject
    }
}
