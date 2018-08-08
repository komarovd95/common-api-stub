package ru.yandex.money.stubs.parking.common.api.application.stop

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.commons.Balance
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.time.ZonedDateTime

data class StopParkingResponse(
    private val sessionId: String,
    private val serverTime: ZonedDateTime,
    private val startTime: ZonedDateTime,
    private val endTime: ZonedDateTime,
    private val refund: Balance
) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val stopParkingResult = JSONObject()

        stopParkingResult["sessionId"] = sessionId
        stopParkingResult["serverTime"] = serverTime.toOffsetDateTime().toString()
        stopParkingResult["startTime"] = startTime.toOffsetDateTime().toString()
        stopParkingResult["endTime"] = endTime.toOffsetDateTime().toString()
        stopParkingResult["refund"] = refund.toJson()

        jsonObject["stopParkingResult"] = stopParkingResult

        return jsonObject
    }
}
