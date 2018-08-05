package ru.yandex.money.stubs.parking.common.api.json

import org.json.JSONObject

operator fun JSONObject.set(key: String, value: Any?) {
    this.put(key, value)
}

operator fun JSONObject.contains(key: String) = this.has(key)
