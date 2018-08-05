package ru.yandex.money.stubs.parking.common.api.process.token

import org.dizitart.no2.Document
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteCollection
import org.dizitart.no2.filters.Filters
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

class NitriteTokenRegistry(db: Nitrite) : TokenRegistry {

    companion object {
        private val log = LoggerFactory.getLogger(NitriteTokenRegistry::class.java)
    }

    private val tokens: NitriteCollection = db.getCollection("tokens")

    init {
        if (!tokens.hasIndex("token")) {
            tokens.createIndex("token", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    override fun create(token: String, expiresAt: ZonedDateTime): Boolean {
        log.info("Inserting token: token={}, expiresAt={}", token, expiresAt)
        val tokenDocument = Document
            .createDocument("token", token)
            .put("expiresAt", expiresAt.toOffsetDateTime().toString())!!
            .put("createdAt", ZonedDateTime.now().toOffsetDateTime().toString())
        val result = tokens.insert(tokenDocument)
        log.info("Inserting token is finished: affectedCount={}", result.affectedCount)
        return result.affectedCount == 1
    }

    override fun contains(token: String) = tokens.find(
        Filters.and(
            Filters.eq("token", token),
            Filters.gt("expiresAt", ZonedDateTime.now().toOffsetDateTime().toString())
        )
    ).size() == 1
}
