package ru.yandex.money.stubs.parking.common.api.application.pay

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.time.ZonedDateTime

data class PayResponse(
    private val sessionId: String,
    private val serverTime: ZonedDateTime,
    private val startTime: ZonedDateTime,
    private val endTime: ZonedDateTime
) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val parkingSession = JSONObject()

        parkingSession["sessionId"] = sessionId
        parkingSession["serverTime"] = serverTime.toOffsetDateTime().toString()
        parkingSession["startTime"] = startTime.toOffsetDateTime().toString()
        parkingSession["endTime"] = endTime.toOffsetDateTime().toString()

        jsonObject["parkingSession"] = parkingSession

        return jsonObject
    }
}
