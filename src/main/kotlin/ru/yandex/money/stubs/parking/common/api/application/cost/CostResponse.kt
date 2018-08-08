package ru.yandex.money.stubs.parking.common.api.application.cost

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.commons.Balance
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

data class CostResponse(
    private val orderId: String,
    private val balance: Balance
) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val orderDetails = JSONObject()

        orderDetails["orderId"] = orderId
        orderDetails["isParkingAccountsSupported"] = true
        orderDetails["cost"] = balance.toJson()

        jsonObject["orderDetails"] = orderDetails

        return jsonObject
    }
}
