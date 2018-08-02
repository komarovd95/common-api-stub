package ru.yandex.money.stubs.parking.common.api.process.token

interface CredentialsRegistry {

    fun resolve(credentials: TokenCredentials): Boolean
}
