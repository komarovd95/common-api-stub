package ru.yandex.money.stubs.parking.common.api.gateways.accounts

import java.math.BigDecimal

interface AccountsGateway {

    fun findAccount(accountNumber: String): Pair<String, BigDecimal>

    fun createAccount(accountNumber: String): Boolean
}