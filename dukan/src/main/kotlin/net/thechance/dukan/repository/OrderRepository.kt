package net.thechance.dukan.repository

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface OrderRepository : JpaRepository<Order, UUID> {
    @Modifying
    @Transactional
    @Query("UPDATE Order order SET order.status = :status WHERE order.id = :orderId")
    fun updateOrderStatus(orderId: UUID, status: Order.OrderStatus): Int
    fun findByIdAndDukanId(id: UUID, dukanId: UUID): Order?
}