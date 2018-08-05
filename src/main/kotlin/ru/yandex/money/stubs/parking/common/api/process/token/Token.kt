package ru.yandex.money.stubs.parking.common.api.process.token

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.time.ZonedDateTime

data class Token(private val token: String, private val expiresAt: ZonedDateTime) : Json {
    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val authorization = JSONObject()
        authorization["token"] = token
        authorization["expiresAt"] = expiresAt.toOffsetDateTime().toString()

        jsonObject["authorization"] = authorization

        return jsonObject
    }
}
