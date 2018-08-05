package ru.yandex.money.stubs.parking.common.api.accounts

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.math.BigDecimal

data class Balance(private val amount: BigDecimal) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        jsonObject["amount"] = amount.toDouble()
        jsonObject["currency"] = CURRENCY

        return jsonObject
    }

    companion object {
        private const val CURRENCY = "RUB"
    }
}
