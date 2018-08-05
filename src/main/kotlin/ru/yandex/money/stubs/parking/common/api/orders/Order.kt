package ru.yandex.money.stubs.parking.common.api.orders

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime

data class Order(
        val orderId: String,
        val parkingId: Long,
        val accountNumber: String,
        val createdAt: ZonedDateTime,
        val duration: Duration,
        val amount: BigDecimal
) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        jsonObject["orderId"] = orderId
        jsonObject["parkingId"] = parkingId
        jsonObject["accountNumber"] = accountNumber
        jsonObject["duration"] = duration.toString()
        jsonObject["createdAt"] = createdAt.toOffsetDateTime().toString()
        jsonObject["amount"] = amount.toPlainString()

        return jsonObject
    }
}
