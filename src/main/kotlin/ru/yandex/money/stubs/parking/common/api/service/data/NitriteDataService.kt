package ru.yandex.money.stubs.parking.common.api.service.data

import org.dizitart.no2.Document
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import org.dizitart.no2.tool.Exporter
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.StringWriter

class NitriteDataService(private val db: Nitrite) : DataService {

    override fun data(): String {
        val exporter = Exporter.of(db)
        val writer = StringWriter()
        exporter.exportTo(writer)
        return writer.toString()
    }

    override fun changeData(method: String, collectionName: String, data: JSONObject) {
        when (method) {
            "create" -> {
                val collection = db.getCollection(collectionName)
                val newDoc = Document(data.toMap())
                val result = collection.insert(newDoc)
                log.info("Create result: result={}", result)
            }
            "update" -> {
                val collection = db.getCollection(collectionName)
                val id = data.getLong("id")
                val found = collection.find(Filters.eq("_id", id))
                val document = found.single()
                data.remove("id")
                document.putAll(data.toMap())
                val result = collection.update(document)
                log.info("Update result: result={}", result)
            }
            "remove" -> {
                val collection = db.getCollection(collectionName)
                val id = data.getLong("id")
                val result = collection.remove(Filters.eq("_id", id))
                log.info("Remove result: result={}", result)
            }
            else -> {
                log.error("Unknown method: method={}", method)
                throw DataException("Unknown method")
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(NitriteDataService::class.java)
    }
}
