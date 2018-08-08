package ru.yandex.money.stubs.parking.common.api.application.deposit

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.commons.Balance
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

data class DepositResponse(private val balance: Balance, private val operatorName: String) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val balanceDetails = JSONObject()

        balanceDetails["balance"] = balance.toJson()
        balanceDetails["operatorName"] = operatorName

        jsonObject["balanceDetails"] = balanceDetails

        return jsonObject
    }
}
