package ru.yandex.money.stubs.parking.common.api.application.balance

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.jsonProperty

class GetBalanceRequest(jsonString: String) {

    private val json = JSONObject(jsonString)

    val parkingAccountNumber by jsonProperty<String>(json)  { throw  Exception() }
    val parkingId by jsonProperty<Long>(json) { throw  Exception() }

    override fun toString() = "GetBalanceRequest(json=$json)"
}
