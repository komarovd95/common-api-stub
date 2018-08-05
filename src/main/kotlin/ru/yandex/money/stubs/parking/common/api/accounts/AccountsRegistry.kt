package ru.yandex.money.stubs.parking.common.api.accounts

interface AccountsRegistry {

    fun findAccount(accountNumber: String): Account
    fun saveAccount(account: Account)
}
