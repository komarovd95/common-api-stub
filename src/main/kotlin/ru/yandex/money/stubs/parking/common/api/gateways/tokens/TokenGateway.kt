package ru.yandex.money.stubs.parking.common.api.gateways.tokens

import java.time.ZonedDateTime

interface TokenGateway {

    fun createToken(token: String, expiresAt: ZonedDateTime): Boolean

    fun findToken(token: String): Pair<String, ZonedDateTime>
}
