package ru.yandex.money.stubs.parking.common.api.application.cost

import ru.yandex.money.stubs.parking.common.api.json.JsonDeserializable
import java.time.Duration

class CostRequest(jsonString: String) : JsonDeserializable(jsonString) {
    val licensePlate by jsonProperty<String>()
    val parkingId by jsonProperty<Long>()
    val parkingAccountNumber by jsonProperty<String>()
    val duration by jsonProperty<Duration>()
}
