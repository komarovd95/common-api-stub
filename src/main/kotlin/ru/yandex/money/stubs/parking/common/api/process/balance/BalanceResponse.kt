package ru.yandex.money.stubs.parking.common.api.process.balance

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.accounts.Balance
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set

data class BalanceResponse(private val balance: Balance, private val operatorName: String) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        val balanceDetails = JSONObject()

        balanceDetails["balance"] = balance.toJson()
        balanceDetails["operatorName"] = operatorName

        jsonObject["balanceDetails"] = balanceDetails

        return jsonObject
    }
}
