package ru.yandex.money.stubs.parking.common.api.orders

interface OrdersRegistry {

    fun createOrder(order: Order)
    fun findOrder(orderId: String): Order
}
