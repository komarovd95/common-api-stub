package ru.yandex.money.stubs.parking.common.api.application.pay

import ru.yandex.money.stubs.parking.common.api.json.JsonDeserializable

class PayRequest(jsonString: String) : JsonDeserializable(jsonString) {
    val orderId by jsonProperty<String>()
}
