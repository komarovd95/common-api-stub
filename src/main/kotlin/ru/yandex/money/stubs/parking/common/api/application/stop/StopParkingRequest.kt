package ru.yandex.money.stubs.parking.common.api.application.stop

import ru.yandex.money.stubs.parking.common.api.json.JsonDeserializable

class StopParkingRequest(jsonString: String) : JsonDeserializable(jsonString) {
    val sessionId by jsonProperty<String>()
}
