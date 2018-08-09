package ru.yandex.money.stubs.parking.common.api.application.sessions

import org.json.JSONArray
import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.time.ZonedDateTime

data class ActiveSessionResponse(
    private val serverTime: ZonedDateTime,
    private val activeSessions: Collection<ActiveSession>
) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val sessions = JSONObject()

        sessions["serverTime"] = serverTime.toOffsetDateTime().toString()

        val list = JSONArray()
        activeSessions.forEach { list.put(it.toJson()) }

        sessions["list"] = list

        jsonObject["sessions"] = sessions

        return jsonObject
    }
}
