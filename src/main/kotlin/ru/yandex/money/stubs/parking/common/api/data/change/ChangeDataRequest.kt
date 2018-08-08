package ru.yandex.money.stubs.parking.common.api.data.change

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.JsonDeserializable

class ChangeDataRequest(jsonString: String) : JsonDeserializable(jsonString) {
    val method by jsonProperty<String>()
    val collectionName by jsonProperty<String>()
    val data by jsonProperty<JSONObject>()
}
