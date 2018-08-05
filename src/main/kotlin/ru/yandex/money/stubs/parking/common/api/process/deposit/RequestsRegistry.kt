package ru.yandex.money.stubs.parking.common.api.process.deposit

interface RequestsRegistry {

    fun add(requestId: String): Boolean

    operator fun contains(requestId: String): Boolean
}
