package ru.yandex.money.stubs.parking.common.api.gateways.accounts

import org.dizitart.no2.Document
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.NitriteGateway
import java.math.BigDecimal

class NitriteAccountsGateway(db: Nitrite) :
        NitriteGateway(db, "accounts", listOf(ACCOUNT_NUMBER_FIELD)), AccountsGateway {

    override fun findAccount(accountNumber: String): Pair<String, BigDecimal> {
        val cursor = collection.find(Filters.eq(ACCOUNT_NUMBER_FIELD, accountNumber))
        if (cursor.size() != 1) {
            throw GatewayException("find_account($accountNumber)")
        }
        val document = cursor.single()
        val balance = BigDecimal(document[BALANCE_FIELD, String::class.java])
        return accountNumber to balance
    }

    override fun createAccount(accountNumber: String): Boolean {
        val document = Document.createDocument(ACCOUNT_NUMBER_FIELD, accountNumber)
                .put(BALANCE_FIELD, BigDecimal.ZERO.toPlainString())
        val result = collection.insert(document)
        return result.affectedCount == 1
    }

    companion object {
        private const val ACCOUNT_NUMBER_FIELD = "accountNumber"
        private const val BALANCE_FIELD = "balance"
    }
}
