package ru.yandex.money.stubs.parking.common.api.application.sessions

import ru.yandex.money.stubs.parking.common.api.json.JsonDeserializable

class ActiveSessionRequest(jsonString: String) : JsonDeserializable(jsonString) {
    val parkingAccountNumber by jsonProperty<String>()
    val licensePlate by jsonProperty<String>()
}
