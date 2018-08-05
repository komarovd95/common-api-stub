package ru.yandex.money.stubs.parking.common.api.data

import org.dizitart.no2.Document
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import org.json.JSONObject
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationException

class DataService(private val db: Nitrite) {

    companion object {
        private val log = LoggerFactory.getLogger(DataService::class.java)
    }

    fun doDataChange(dataRequest: DataRequest) {
        try {
            when (dataRequest.method) {
                "create" -> {
                    if (dataRequest.collectionName == null) {
                        throw ApplicationException(ApplicationError.TechnicalError)
                    }
                    val collection = db.getCollection(dataRequest.collectionName)

                    val jsonObject = JSONObject(dataRequest.data)
                    val newDoc = Document(jsonObject.toMap())
                    val result = collection.insert(newDoc)
                    log.info("Insert result: document={}, result={}", newDoc, result)
                }
                "update" -> {
                    if (dataRequest.collectionName == null) {
                        throw ApplicationException(ApplicationError.TechnicalError)
                    }
                    val collection = db.getCollection(dataRequest.collectionName)

                    val jsonObject = JSONObject(dataRequest.data)
                    val id = jsonObject.getLong("id")

                    val found = collection.find(Filters.eq("_id", id))
                    val document = found.single()

                    jsonObject.remove("id")
                    document.putAll(jsonObject.toMap())

                    val result = collection.update(document)
                    log.info("Update result: document={}, result={}", document, result)
                }
                "remove" -> {
                    if (dataRequest.collectionName == null) {
                        throw ApplicationException(ApplicationError.TechnicalError)
                    }
                    val collection = db.getCollection(dataRequest.collectionName)

                    val jsonObject = JSONObject(dataRequest.data)
                    val id = jsonObject.getLong("id")

                    val result = collection.remove(Filters.eq("_id", id))
                    log.info("Remove result: id={}, result={}", id, result)
                }
                else -> {
                    log.error("Unknown method: method={}", dataRequest.method)
                }
            }
        } catch (ex: Exception) {
            throw ApplicationException(ApplicationError(500, ex.message!!))
        }
    }
}
