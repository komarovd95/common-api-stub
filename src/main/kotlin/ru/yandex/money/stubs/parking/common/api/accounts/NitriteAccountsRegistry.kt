package ru.yandex.money.stubs.parking.common.api.accounts

import org.dizitart.no2.*
import org.dizitart.no2.filters.Filters
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.data.DataException
import java.math.BigDecimal

class NitriteAccountsRegistry(db: Nitrite) : AccountsRegistry {

    companion object {
        private val log = LoggerFactory.getLogger(NitriteAccountsRegistry::class.java)
    }

    private val accounts = db.getCollection("accounts")

    init {
        if (!accounts.hasIndex("accountNumber")) {
            accounts.createIndex("accountNumber", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    override fun findAccount(accountNumber: String): Account {
        log.info("Finding account in db: accountNumber={}", accountNumber)
        val cursor = accounts.find(Filters.eq("accountNumber", accountNumber))
        if (cursor.size() != 1) {
            log.warn("Failed to find account in db: accountNumber={}")
            throw DataException("FIND_ACCOUNT($accountNumber)")
        }
        val document = cursor.single()
        val balanceValue = document["balance", String::class.java]
        val balance = Balance(BigDecimal(balanceValue))
        val account = Account(accountNumber, balance)
        log.info("Successful find account: account={}", account)
        return account
    }

    override fun saveAccount(account: Account) {
        log.info("Saving account: account={}", account)

        val result = accounts.update(
                Filters.eq("accountNumber", account.accountNumber),
                Document.createDocument("balance", account.balance.amount.toPlainString()),
                UpdateOptions.updateOptions(true)
        )
        log.info("Saving account is finished: result={}", result)
    }
}
