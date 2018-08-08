package ru.yandex.money.stubs.parking.common.api.service.token

import java.time.ZonedDateTime

data class Token(val token: String, val expiresAt: ZonedDateTime)
