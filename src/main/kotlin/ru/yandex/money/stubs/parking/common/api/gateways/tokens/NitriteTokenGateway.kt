package ru.yandex.money.stubs.parking.common.api.gateways.tokens

import org.dizitart.no2.Document
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.NitriteGateway
import ru.yandex.money.stubs.parking.common.api.service.token.Token
import java.time.ZonedDateTime

class NitriteTokenGateway(db: Nitrite) : NitriteGateway(db, "tokens", listOf(TOKEN_FIELD)), TokenGateway {

    override fun createToken(token: String, expiresAt: ZonedDateTime): Boolean {
        val tokenDocument = Document
                .createDocument(TOKEN_FIELD, token)
                .put(EXPIRES_AT_FIELD, expiresAt.toOffsetDateTime().toString())!!
                .put(CREATED_AT_FIELD, ZonedDateTime.now().toOffsetDateTime().toString())
        return collection.insert(tokenDocument).affectedCount == 1
    }

    override fun findToken(token: String): Pair<String, ZonedDateTime> {
        val found =  collection.find(Filters.eq(TOKEN_FIELD, token))
        if (found.size() != 1) {
            throw GatewayException("find_token")
        }
        val document = found.single()
        val expiresAt = ZonedDateTime.parse(document[EXPIRES_AT_FIELD, String::class.java])
        return token to expiresAt
    }

    companion object {
        private const val TOKEN_FIELD = "token"
        private const val EXPIRES_AT_FIELD = "expiresAt"
        private const val CREATED_AT_FIELD = "createdAt"
    }
}
