package ru.yandex.money.stubs.parking.common.api.process.deposit

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.accounts.Balance
import ru.yandex.money.stubs.parking.common.api.json.contains
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import java.io.Reader
import java.math.BigDecimal

class DepositRequest private constructor(
        val requestId: String,
        val parkingId: String,
        val accountNumber: String,
        val money: Balance
) {

    companion object {
        val Deserializer: (Reader) -> Any = deserializer@{
            val jsonObject = JSONObject(it.readText())

            if ("requestId" !in jsonObject) {
                  return@deserializer ApplicationError.InvalidRequestId
            }
            if ("parkingId" !in jsonObject) {
                return@deserializer ApplicationError.InvalidParkingId
            }
            if ("parkingAccountNumber" !in jsonObject) {
                return@deserializer ApplicationError.InvalidParkingAccountNumber
            }
            if ("money" !in jsonObject) {
                return@deserializer ApplicationError.InvalidMoney
            }

            val money = jsonObject.getJSONObject("money")

            if ("amount" !in money) {
                return@deserializer ApplicationError.InvalidAmount
            }

            DepositRequest(
                    requestId = jsonObject.getString("requestId"),
                    parkingId = jsonObject.getString("parkingId"),
                    accountNumber = jsonObject.getString("parkingAccountNumber"),
                    money = Balance(BigDecimal(money.getDouble("amount")))
            )
        }
    }
}
