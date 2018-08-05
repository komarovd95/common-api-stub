package ru.yandex.money.stubs.parking.common.api.process.balance

import ru.yandex.money.stubs.parking.common.api.accounts.AccountsRegistry
import ru.yandex.money.stubs.parking.common.api.data.DataException
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException

class BalanceService(private val accountsRegistry: AccountsRegistry, private val operatorName: String) {

    fun getBalance(balanceRequest: BalanceRequest): BalanceResponse {
        try {
            val account = accountsRegistry.findAccount(balanceRequest.parkingAccountNumber)
            return BalanceResponse(account.balance, operatorName)
        } catch (ex: DataException) {
            throw ApplicationException(ApplicationError.ParkingAccountNotFound)
        }
    }
}