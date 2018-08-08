package ru.yandex.money.stubs.parking.common.api.service.deposit

import java.util.concurrent.ConcurrentHashMap

class DepositService {

    private val requestIds = ConcurrentHashMap<String, Boolean>()

    fun isDepositDone(requestId: String) = requestIds.containsKey(requestId)

    fun depositDone(requestId: String) {
        requestIds[requestId] = true
    }
}
