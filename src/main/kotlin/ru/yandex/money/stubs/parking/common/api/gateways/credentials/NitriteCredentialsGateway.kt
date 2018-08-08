package ru.yandex.money.stubs.parking.common.api.gateways.credentials

import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteCollection
import org.dizitart.no2.filters.Filters
import ru.yandex.money.stubs.parking.common.api.gateways.NitriteGateway

class NitriteCredentialsGateway(
        db: Nitrite,
        init: (NitriteCollection) -> Unit = {}
) : NitriteGateway(db, "credentials", listOf(LOGIN_FIELD), init), CredentialsGateway {

    override fun resolve(login: String, password: String) = collection.find(
            Filters.and(
                    Filters.eq(LOGIN_FIELD, login),
                    Filters.eq(PASSWORD_FIELD, password)
            )
    ).size() == 1

    companion object {
        const val LOGIN_FIELD = "login"
        const val PASSWORD_FIELD = "password"
    }
}
