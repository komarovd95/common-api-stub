package ru.yandex.money.stubs.parking.common.api.gateways.orders

interface OrderGateway {

    fun createNewOrder(order: Order): Boolean

    fun findActiveOrder(parkingId: String, accountNumber: String, licensePlate: String): Order

    fun findActiveOrder(orderId: String): Order

    fun findActiveOrders(accountNumber: String, licensePlate: String): Collection<Order>

    fun findPaidOrder(sessionId: String): Order

    fun updateOrder(order: Order)
}
