package ru.yandex.money.stubs.parking.common.api.data.change

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

class ChangeDataResponse : Json {
    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject["status"] = "ok"
        return jsonObject
    }
}
