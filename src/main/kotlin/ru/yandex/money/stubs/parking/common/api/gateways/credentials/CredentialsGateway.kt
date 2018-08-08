package ru.yandex.money.stubs.parking.common.api.gateways.credentials

interface CredentialsGateway {

    fun resolve(login: String, password: String): Boolean
}
