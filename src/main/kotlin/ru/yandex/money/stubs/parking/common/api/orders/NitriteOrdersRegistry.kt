package ru.yandex.money.stubs.parking.common.api.orders

import org.dizitart.no2.Document
import org.dizitart.no2.IndexOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.filters.Filters
import org.slf4j.LoggerFactory
import ru.yandex.money.stubs.parking.common.api.data.DataException
import java.math.BigDecimal
import java.time.Duration
import java.time.ZonedDateTime

class NitriteOrdersRegistry(private val db: Nitrite) : OrdersRegistry {

    companion object {
        private val log = LoggerFactory.getLogger(NitriteOrdersRegistry::class.java)
    }

    init {
        val orders = db.getCollection("orders")
        if (!orders.hasIndex("orderId")) {
            orders.createIndex("orderId", IndexOptions.indexOptions(IndexType.Unique))
        }
    }

    override fun createOrder(order: Order) {
        log.info("Creating order: order={}", order)
        val orders = db.getCollection("orders")

        val document = Document.createDocument("orderId", order.orderId)
                .put("parkingId", order.parkingId)!!
                .put("accountNumber", order.accountNumber)!!
                .put("createdAt", order.createdAt.toOffsetDateTime().toString())!!
                .put("duration", order.duration.toString())!!
                .put("amount", order.amount.toPlainString())!!

        val result = orders.insert(document)
        log.info("Creating order finished: result={}", result)
    }

    override fun findOrder(orderId: String): Order {
        log.info("Finding order: orderId={}", orderId)
        val orders = db.getCollection("orders")
        val cursor = orders.find(Filters.eq("orderId", orderId))
        if (cursor.size() != 1) {
            log.warn("Failed to find order: orderId={}", orderId)
            throw DataException("FIND_ORDER($orderId)")
        }
        val document = cursor.single()

        val parkingId = document["parkingId", Long::class.java]
        val accountNumber= document["accountNumber", String::class.java]
        val createdAt = ZonedDateTime.parse(document["createdAt", String::class.java])
        val duration= Duration.parse(document["duration", String::class.java])
        val amount = BigDecimal(document["amount", String::class.java])

        val order = Order(
                orderId = orderId,
                parkingId = parkingId,
                accountNumber = accountNumber,
                createdAt = createdAt,
                duration = duration,
                amount = amount
        )
        log.info("Finding order finished: order={}", order)
        return order
    }
}
