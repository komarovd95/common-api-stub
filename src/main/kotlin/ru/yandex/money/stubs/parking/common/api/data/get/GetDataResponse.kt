package ru.yandex.money.stubs.parking.common.api.data.get

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json

data class GetDataResponse(private val json: String) : Json {
    override fun toJson() = JSONObject(json)
}
