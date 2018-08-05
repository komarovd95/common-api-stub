package ru.yandex.money.stubs.parking.common.api.data

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import java.io.Reader

data class DataRequest(val method: String, val collectionName: String?, val data: String) {

    companion object {
        val Deserializer: (Reader) -> Any = {
            val jsonObject = JSONObject(it.readText())

            if (jsonObject.has("method")) {
                val method = jsonObject.getString("method")

                val collectionName = if (jsonObject.has("collectionName")) {
                    jsonObject.getString("collectionName")
                } else {
                    null
                }

                if (jsonObject.has("data")) {
                    val data = jsonObject.getJSONObject("data")
                    DataRequest(method, collectionName, data.toString())
                } else {
                    ApplicationError.TechnicalError
                }
            } else {
                ApplicationError.TechnicalError
            }
        }
    }
}
