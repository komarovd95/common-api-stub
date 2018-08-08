package ru.yandex.money.stubs.parking.common.api.service.accounts

interface AccountService {

    fun findAccount(accountNumber: String): Account
}
