package ru.yandex.money.stubs.parking.common.api.service.order

import java.time.ZonedDateTime

data class SessionInfo(val sessionId: String, val startTime: ZonedDateTime, val endTime: ZonedDateTime)
