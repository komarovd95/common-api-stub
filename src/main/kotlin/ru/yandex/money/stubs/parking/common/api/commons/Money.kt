package ru.yandex.money.stubs.parking.common.api.commons

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.JsonDeserializable
import java.math.BigDecimal

class Money(json: JSONObject) : JsonDeserializable(json) {
    val amount by jsonProperty<BigDecimal>()
    val currency by jsonProperty(onMissingProperty = { "RUB" })
}
