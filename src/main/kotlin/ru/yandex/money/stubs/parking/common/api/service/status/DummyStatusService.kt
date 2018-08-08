package ru.yandex.money.stubs.parking.common.api.service.status

class DummyStatusService(private val active: Boolean = true) : StatusService {
    override fun isActive() = active
}
