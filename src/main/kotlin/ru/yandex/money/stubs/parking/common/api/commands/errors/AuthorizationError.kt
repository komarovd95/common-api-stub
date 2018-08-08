package ru.yandex.money.stubs.parking.common.api.commands.errors

class AuthorizationError private constructor(errorCode: Int, errorMessage: String) :
        ApplicationError(errorCode, errorMessage) {

    companion object {
        val InvalidCredentials = AuthorizationError(101, "Login or password is incorrect")
        val ExpiredToken = AuthorizationError(102, "Token is expired")
    }
}
