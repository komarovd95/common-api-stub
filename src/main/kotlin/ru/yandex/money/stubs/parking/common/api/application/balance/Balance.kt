package ru.yandex.money.stubs.parking.common.api.application.balance

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.math.BigDecimal

data class Balance(val amount: BigDecimal, val currency: String = "RUB") : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        jsonObject["amount"] = amount.toDouble()
        jsonObject["currency"] = currency

        return jsonObject
    }
}
