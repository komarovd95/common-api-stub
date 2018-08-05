package ru.yandex.money.stubs.parking.common.api.json

import org.json.JSONObject

interface Json {
    fun toJson(): JSONObject
}
