package ru.yandex.money.stubs.parking.common.api.application.deposit

import ru.yandex.money.stubs.parking.common.api.commons.Money
import ru.yandex.money.stubs.parking.common.api.json.JsonDeserializable

class DepositRequest(jsonString: String) : JsonDeserializable(jsonString) {
    val requestId by jsonProperty<String>()
    val parkingId by jsonProperty<Long>()
    val parkingAccountNumber by jsonProperty<String>()
    val money by jsonProperty(onComplexType = { Money(it) })
}
