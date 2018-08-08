package ru.yandex.money.stubs.parking.common.api.service.order

import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.orders.Order
import ru.yandex.money.stubs.parking.common.api.gateways.orders.OrderGateway
import ru.yandex.money.stubs.parking.common.api.gateways.orders.OrderStatus
import ru.yandex.money.stubs.parking.common.api.service.accounts.AccountService
import ru.yandex.money.stubs.parking.common.api.service.parkings.ParkingService
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID

class OrderService(
    private val orderGateway: OrderGateway,
    private val parkingService: ParkingService,
    private val accountService: AccountService
) {

    fun storeOrder(order: CreatingOrder): OrderInfo {
        return try {
            val existingOrder = orderGateway.findActiveOrder(
                order.parking.parkingId,
                order.account.accountNumber,
                order.licensePlate
            )
            log.info("Found existing active order: order={}", existingOrder)
            val endTime = existingOrder.startTime.plus(existingOrder.duration)
            if (endTime.isBefore(ZonedDateTime.now())) {
                log.warn("Order is in incorrect status: order={}", existingOrder)
                updateOrder(existingOrder.copy(status = OrderStatus.CANCELLED))
                return createNewOrder(order)
            }
            val now = ZonedDateTime.now()
            val updatedOrder = if (existingOrder.status == OrderStatus.INIT) {
                existingOrder.copy(
                    amount = order.cost,
                    startTime = now,
                    duration = order.duration
                )
            } else {
                val newStatus = if (order.cost.compareTo(BigDecimal.ZERO) == 0) {
                    existingOrder.status
                } else {
                    OrderStatus.INIT
                }

                existingOrder.copy(
                    amount = existingOrder.amount + order.cost,
                    duration = existingOrder.duration.plus(order.duration),
                    status = newStatus
                )
            }
            updateOrder(updatedOrder)
            OrderInfo(
                updatedOrder.orderId,
                updatedOrder.amount - updatedOrder.paid,
                updatedOrder.accountNumber,
                updatedOrder
            )
        } catch (ex: GatewayException) {
            createNewOrder(order)
        }
    }

    private fun createNewOrder(order: CreatingOrder): OrderInfo {
        val now = ZonedDateTime.now()
        log.info("Creating new order: order={}", order)
        val newOrder = Order(
            orderId = newReference(),
            parkingId = order.parking.parkingId,
            licensePlate = order.licensePlate,
            accountNumber = order.account.accountNumber,
            startTime = now,
            duration = order.duration,
            amount = order.cost,
            paid = BigDecimal.ZERO,
            status = OrderStatus.INIT,
            sessionReference = newReference()
        )
        log.info("Saving new order: newOrder={}", newOrder)
        val creationResult = orderGateway.createNewOrder(newOrder)
        if (!creationResult) {
            log.warn("Failed to save new order: newOrder={}", newOrder)
            throw OrderException("Failed to save new order")
        }
        return OrderInfo(newOrder.orderId, newOrder.amount, newOrder.accountNumber, newOrder)
    }

    private fun updateOrder(order: Order) {
        log.info("Updating order: order={}", order)
        try {
            orderGateway.updateOrder(order)
        } catch (ex: GatewayException) {
            log.warn("Failed to update order: order={}", order)
            throw OrderException("Failed to update order")
        }
    }

    fun findOrder(orderId: String): OrderInfo {
        log.info("Finding order with ID: orderId={}", orderId)
        return try {
            val order = orderGateway.findActiveOrder(orderId)
            OrderInfo(orderId, order.amount - order.paid, order.accountNumber, order)
        } catch (ex: GatewayException) {
            log.warn("Failed to find active order by ID: orderId={}", orderId, ex)
            throw OrderException("Failed to find active order by ID")
        }
    }

    fun payForOrder(orderInfo: OrderInfo): SessionInfo {
        log.info("Paying for order: orderInfo={}", orderInfo)
        val order = orderInfo.order
        val updatedOrder = order.copy(paid = order.paid + orderInfo.amountToPay, status = OrderStatus.PAID)
        updateOrder(updatedOrder)
        return SessionInfo(order.sessionReference, order.startTime, order.startTime.plus(order.duration))
    }

    fun stopSession(sessionId: String): Pair<SessionInfo, BigDecimal> {
        log.info("Stopping session: sessionId={}", sessionId)
        return try {
            val order = orderGateway.findPaidOrder(sessionId)
            val stoppedDuration = Duration.between(order.startTime, ZonedDateTime.now())
            val refundableDuration = order.duration.minus(stoppedDuration)
            val parkingInfo = parkingService.findParkingInfo(order.parkingId.toLong())
            val refundAmount = parkingInfo.tariff.multiply(refundableDuration.toBigDecimal())
                .setScale(2, RoundingMode.HALF_UP)

            val updatedOrder = order.copy(
                duration = stoppedDuration,
                paid = order.paid - refundAmount,
                status = OrderStatus.CANCELLED
            )
            updateOrder(updatedOrder)

            val account = accountService.findAccount(order.accountNumber)
            accountService.updateAccount(account.copy(balance = account.balance + refundAmount))

            SessionInfo(order.sessionReference, order.startTime, order.startTime.plus(stoppedDuration)) to refundAmount
        } catch (ex: GatewayException) {
            log.warn("Failed to stop session: sessionId={}", sessionId)
            throw OrderException("Failed to stop session")
        }
    }

    private fun newReference() = UUID.randomUUID().toString().replace("-", "")

    private fun Duration.toBigDecimal() = BigDecimal(this.toMinutes() / 60.0)

    companion object {
        private val log = LoggerFactory.getLogger(OrderService::class.java)
    }
}
