package ru.yandex.money.stubs.parking.common.api.gateways

import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteCollection

abstract class NitriteGateway(
        private val db: Nitrite,
        private val collectionName: String,
        indexes: Collection<String> = emptyList(),
        init: (NitriteCollection) -> Unit = {}
) {

    init {
        val collection = collection
        indexes.forEach { index ->
            if (!collection.hasIndex(index)) {
                collection.createIndex(index, IndexOptions.indexOptions(IndexType.Unique))
            }
        }

        init(collection)
    }

    protected val collection: NitriteCollection get() = db.getCollection(collectionName)
}
