package ru.yandex.money.stubs.parking.common.api.gateways.orders

import org.dizitart.no2.Document
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.gateways.GatewayException
import ru.yandex.money.stubs.parking.common.api.gateways.NitriteGateway
import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime

class NitriteOrderGateway(db: Nitrite) :
    NitriteGateway(db, "orders", listOf(ORDER_ID_FIELD)), OrderGateway {

    override fun createNewOrder(order: Order): Boolean {
        log.info("Creating new order: order={}", order)
        val orderToSave = Document.createDocument(ORDER_ID_FIELD, order.orderId)
            .put(PARKING_ID_FIELD, order.parkingId)!!
            .put(LICENSE_PLATE_FIELD, order.licensePlate)!!
            .put(ACCOUNT_NUMBER_FIELD, order.accountNumber)!!
            .put(START_TIME_FIELD, order.startTime.toOffsetDateTime().toString())!!
            .put(DURATION_FIELD, order.duration.toString())!!
            .put(AMOUNT_FIELD, order.amount.toPlainString())!!
            .put(PAID_FIELD, order.paid.toPlainString())!!
            .put(STATUS_FIELD, order.status.code)!!
            .put(SESSION_REFERENCE_FIELD, order.sessionReference)!!
        val result = collection.insert(orderToSave)
        return result.affectedCount == 1
    }

    override fun findActiveOrder(parkingId: String, accountNumber: String, licensePlate: String): Order {
        log.info("Finding active order: parkingId={}, accountNumber={}, licensePlate={}",
            parkingId, accountNumber, licensePlate)
        val cursor = collection.find(
            Filters.and(
                Filters.eq(PARKING_ID_FIELD, parkingId),
                Filters.eq(ACCOUNT_NUMBER_FIELD, accountNumber),
                Filters.eq(LICENSE_PLATE_FIELD, licensePlate),
                Filters.not(
                    Filters.eq(STATUS_FIELD, OrderStatus.CANCELLED.code)
                )
            ))
        if (cursor.size() != 1) {
            log.warn("Active order not found")
            throw GatewayException("find_active_order($parkingId, $accountNumber, $licensePlate)")
        }
        val document = cursor.single()
        return Order(
            orderId = document[ORDER_ID_FIELD, String::class.java],
            parkingId = document[PARKING_ID_FIELD, String::class.java],
            licensePlate = document[LICENSE_PLATE_FIELD, String::class.java],
            accountNumber = document[ACCOUNT_NUMBER_FIELD, String::class.java],
            startTime = ZonedDateTime.parse(document[START_TIME_FIELD, String::class.java]),
            duration = Duration.parse(document[DURATION_FIELD, String::class.java]),
            amount = BigDecimal(document[AMOUNT_FIELD, String::class.java]),
            paid = BigDecimal(document[PAID_FIELD, String::class.java]),
            status = OrderStatus.byCode(document[STATUS_FIELD, String::class.java]),
            sessionReference = document[SESSION_REFERENCE_FIELD, String::class.java]
        )
    }

    override fun findActiveOrder(orderId: String): Order {
        log.info("Finding active order: orderId={}", orderId)
        val cursor = collection.find(
            Filters.and(
                Filters.eq(ORDER_ID_FIELD, orderId),
                Filters.not(
                    Filters.eq(STATUS_FIELD, OrderStatus.CANCELLED.code)
                )
            )
        )
        if (cursor.size() != 1) {
            log.warn("Active order not found")
            throw GatewayException("find_active_order($orderId)")
        }
        val document = cursor.single()
        return Order(
            orderId = document[ORDER_ID_FIELD, String::class.java],
            parkingId = document[PARKING_ID_FIELD, String::class.java],
            licensePlate = document[LICENSE_PLATE_FIELD, String::class.java],
            accountNumber = document[ACCOUNT_NUMBER_FIELD, String::class.java],
            startTime = ZonedDateTime.parse(document[START_TIME_FIELD, String::class.java]),
            duration = Duration.parse(document[DURATION_FIELD, String::class.java]),
            amount = BigDecimal(document[AMOUNT_FIELD, String::class.java]),
            paid = BigDecimal(document[PAID_FIELD, String::class.java]),
            status = OrderStatus.byCode(document[STATUS_FIELD, String::class.java]),
            sessionReference = document[SESSION_REFERENCE_FIELD, String::class.java]
        )
    }

    override fun findActiveOrders(accountNumber: String, licensePlate: String): Collection<Order> {
        log.info("Find active orders: accountNumber={}, licensePlate={}", accountNumber, licensePlate)
        val cursor = collection.find(
            Filters.and(
                Filters.eq(ACCOUNT_NUMBER_FIELD, accountNumber),
                Filters.eq(LICENSE_PLATE_FIELD, licensePlate),
                Filters.or(
                    Filters.eq(STATUS_FIELD, OrderStatus.PAID.code),
                    Filters.eq(STATUS_FIELD, OrderStatus.PROLONGED.code)
                )
            )
        )
        return cursor.map {
            Order(
                orderId = it[ORDER_ID_FIELD, String::class.java],
                parkingId = it[PARKING_ID_FIELD, String::class.java],
                licensePlate = it[LICENSE_PLATE_FIELD, String::class.java],
                accountNumber = it[ACCOUNT_NUMBER_FIELD, String::class.java],
                startTime = ZonedDateTime.parse(it[START_TIME_FIELD, String::class.java]),
                duration = Duration.parse(it[DURATION_FIELD, String::class.java]),
                amount = BigDecimal(it[AMOUNT_FIELD, String::class.java]),
                paid = BigDecimal(it[PAID_FIELD, String::class.java]),
                status = OrderStatus.byCode(it[STATUS_FIELD, String::class.java]),
                sessionReference = it[SESSION_REFERENCE_FIELD, String::class.java]
            )
        }.filter {
            val endTime = it.startTime.plus(it.duration)
            endTime.isAfter(ZonedDateTime.now())
        }
    }

    override fun findPaidOrder(sessionId: String): Order {
        log.info("Finding paid order: sessionId={}", sessionId)
        val cursor = collection.find(
            Filters.and(
                Filters.eq(SESSION_REFERENCE_FIELD, sessionId),
                Filters.eq(STATUS_FIELD, OrderStatus.PAID.code)
            )
        )
        if (cursor.size() != 1) {
            log.warn("Active order not found")
            throw GatewayException("find_paid_order($sessionId)")
        }
        val document = cursor.single()
        return Order(
            orderId = document[ORDER_ID_FIELD, String::class.java],
            parkingId = document[PARKING_ID_FIELD, String::class.java],
            licensePlate = document[LICENSE_PLATE_FIELD, String::class.java],
            accountNumber = document[ACCOUNT_NUMBER_FIELD, String::class.java],
            startTime = ZonedDateTime.parse(document[START_TIME_FIELD, String::class.java]),
            duration = Duration.parse(document[DURATION_FIELD, String::class.java]),
            amount = BigDecimal(document[AMOUNT_FIELD, String::class.java]),
            paid = BigDecimal(document[PAID_FIELD, String::class.java]),
            status = OrderStatus.byCode(document[STATUS_FIELD, String::class.java]),
            sessionReference = document[SESSION_REFERENCE_FIELD, String::class.java]
        )
    }

    override fun updateOrder(order: Order) {
        log.info("Updating order: order={}", order)
        val orderToUpdate = Document.createDocument(ORDER_ID_FIELD, order.orderId)
            .put(PARKING_ID_FIELD, order.parkingId)!!
            .put(LICENSE_PLATE_FIELD, order.licensePlate)!!
            .put(ACCOUNT_NUMBER_FIELD, order.accountNumber)!!
            .put(START_TIME_FIELD, order.startTime.toOffsetDateTime().toString())!!
            .put(DURATION_FIELD, order.duration.toString())!!
            .put(AMOUNT_FIELD, order.amount.toPlainString())!!
            .put(PAID_FIELD, order.paid.toPlainString())!!
            .put(STATUS_FIELD, order.status.code)!!
            .put(SESSION_REFERENCE_FIELD, order.sessionReference)!!
        val updateResult = collection.update(Filters.eq(ORDER_ID_FIELD, order.orderId), orderToUpdate)
        if (updateResult.affectedCount != 1) {
            throw GatewayException("update_order($order)")
        }
        log.info("Updated order successfully")
    }

    companion object {
        private val log = LoggerFactory.getLogger(NitriteOrderGateway::class.java)

        private const val ORDER_ID_FIELD = "orderId"
        private const val PARKING_ID_FIELD = "parkingId"
        private const val LICENSE_PLATE_FIELD = "licensePlate"
        private const val ACCOUNT_NUMBER_FIELD = "accountNumber"
        private const val START_TIME_FIELD = "startTime"
        private const val DURATION_FIELD = "duration"
        private const val AMOUNT_FIELD = "amount"
        private const val PAID_FIELD = "paid"
        private const val STATUS_FIELD = "status"
        private const val SESSION_REFERENCE_FIELD = "sessionReference"
    }
}
