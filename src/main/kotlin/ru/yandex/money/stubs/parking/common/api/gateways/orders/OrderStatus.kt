package ru.yandex.money.stubs.parking.common.api.gateways.orders

enum class OrderStatus(val code: String) {
    INIT("init"), PAID("paid"), PROLONGED("prolonged"), CANCELLED("cancelled");

    companion object {
        fun byCode(code: String): OrderStatus {
            for (orderStatus in values()) {
                if (orderStatus.code == code) {
                    return orderStatus
                }
            }
            throw IllegalArgumentException(code)
        }
    }
}
