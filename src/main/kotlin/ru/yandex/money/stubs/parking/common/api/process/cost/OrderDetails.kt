package ru.yandex.money.stubs.parking.common.api.process.cost

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.accounts.Balance
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import ru.yandex.money.stubs.parking.common.api.orders.Order

class OrderDetails(private val order: Order) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val orderDetails = JSONObject()

        orderDetails["orderId"] = order.orderId
        orderDetails["isParkingAccountsSupported"] = true
        orderDetails["cost"] = Balance(order.amount).toJson()

        jsonObject["orderDetails"] = orderDetails

        return jsonObject
    }
}
