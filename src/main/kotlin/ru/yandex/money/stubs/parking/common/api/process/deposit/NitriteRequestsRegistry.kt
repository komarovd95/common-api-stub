package ru.yandex.money.stubs.parking.common.api.process.deposit

import org.dizitart.no2.Document
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters

class NitriteRequestsRegistry(private val db: Nitrite) : RequestsRegistry {

    init {
        val requests = db.getCollection(REQUESTS_COLLECTION)
        if (!requests.hasIndex("requestId")) {
            requests.createIndex("requestId", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    override fun contains(requestId: String) =
            db.getCollection(REQUESTS_COLLECTION)
                    .find(Filters.eq("requestId", requestId))
                    .size() == 1

    override fun add(requestId: String) =
            db.getCollection(REQUESTS_COLLECTION)
                    .insert(Document.createDocument("requestId", requestId))
                    .affectedCount == 1

    companion object {
        private const val REQUESTS_COLLECTION = "requests"
    }
}
