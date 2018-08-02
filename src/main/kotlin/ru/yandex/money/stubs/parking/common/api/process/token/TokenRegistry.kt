package ru.yandex.money.stubs.parking.common.api.process.token

import java.time.ZonedDateTime

interface TokenRegistry {

    fun create(token: String, expiresAt: ZonedDateTime)

    operator fun contains(token: String): Boolean
}