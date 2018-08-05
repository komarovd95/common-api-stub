package ru.yandex.money.stubs.parking.common.api.process.token

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.json.Json
import ru.yandex.money.stubs.parking.common.api.json.set
import ru.yandex.money.stubs.parking.common.api.process.error.ApplicationError
import java.io.Reader

data class TokenCredentials(val login: String, val password: String) : Json {

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()

        jsonObject[LOGIN] = login
        jsonObject[PASSWORD] = password

        return jsonObject
    }

    companion object {

        private const val LOGIN = "login"
        private const val PASSWORD = "password"

        val Deserializer: (Reader) -> Any = {
            val jsonObject = JSONObject(it.readText())

            if (!jsonObject.has(LOGIN) || jsonObject.getString(LOGIN).isEmpty()) {
                ApplicationError.InvalidCredentials
            } else if (!jsonObject.has(PASSWORD) || jsonObject.getString(PASSWORD).isEmpty()) {
                ApplicationError.InvalidCredentials
            } else {
                TokenCredentials(jsonObject.getString(LOGIN), jsonObject.getString(PASSWORD))
            }
        }
    }
}
