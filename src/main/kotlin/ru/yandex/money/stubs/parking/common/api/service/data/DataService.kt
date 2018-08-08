package ru.yandex.money.stubs.parking.common.api.service.data

import org.json.JSONObject

interface DataService {
    fun data(): String
    fun changeData(method: String, collectionName: String, data: JSONObject)
}
