package ru.yandex.money.stubs.parking.common.api.process.token

import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteCollection
import org.dizitart.no2.filters.Filters

class NitriteCredentialsRegistry(db: Nitrite, initializer: (NitriteCollection) -> Unit = {}) :
    CredentialsRegistry {

    private val credentials = db.getCollection("credentials")

    init {
        if (!credentials.hasIndex("login")) {
            credentials.createIndex("login", IndexOptions.indexOptions(IndexType.Unique))
        }

        initializer(credentials)
    }

    override fun resolve(credentials: TokenCredentials) = this.credentials.find(
        Filters.and(
            Filters.eq("login", credentials.login),
            Filters.eq("password", credentials.password)
        )
    ).size() == 1
}
