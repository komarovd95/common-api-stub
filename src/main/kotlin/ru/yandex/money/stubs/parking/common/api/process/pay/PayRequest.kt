package ru.yandex.money.stubs.parking.common.api.process.pay

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.contains
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import java.io.Reader

class PayRequest(val orderId: String) {

    companion object {
        val Deserializer: (Reader) -> Any = {
            val jsonObject = JSONObject(it.readText())

            if ("orderId" !in jsonObject) {
                ApplicationError.InvalidOrderId
            } else {
                PayRequest(jsonObject.getString("orderId"))
            }
        }
    }
}