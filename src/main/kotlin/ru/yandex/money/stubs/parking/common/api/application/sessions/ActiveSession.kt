package ru.yandex.money.stubs.parking.common.api.application.sessions

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.time.ZonedDateTime

data class ActiveSession(
    private val sessionId: String,
    private val licensePlate: String,
    private val parkingId: String,
    private val parkingName: String,
    private val parkingPaymentType: String = "prepay",
    private val startTime: ZonedDateTime,
    private val endTime: ZonedDateTime
) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        jsonObject["sessionId"] = sessionId
        jsonObject["licensePlate"] = licensePlate
        jsonObject["parkingId"] = parkingId
        jsonObject["parkingName"] = parkingName
        jsonObject["parkingPaymentType"] = parkingPaymentType
        jsonObject["startTime"] = startTime.toOffsetDateTime().toString()
        jsonObject["endTime"] = endTime.toOffsetDateTime().toString()

        return jsonObject
    }
}
