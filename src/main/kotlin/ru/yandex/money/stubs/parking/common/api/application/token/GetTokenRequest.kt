package ru.yandex.money.stubs.parking.common.api.application.token

import org.json.JSONObject
import ru.yandex.money.stubs.parking.common.api.commands.errors.AuthorizationError
import ru.yandex.money.stubs.parking.common.api.json.jsonProperty

class GetTokenRequest(jsonString: String) {

    private val json = JSONObject(jsonString)

    val login by jsonProperty<String>(json) { throw AuthorizationError.InvalidCredentials }
    val password by jsonProperty<String>(json) { throw AuthorizationError.InvalidCredentials }

    override fun toString(): String {
        return "GetTokenRequest(json=$json)"
    }
}
