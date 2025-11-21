package net.thechance.dukan.repository

import net.thechance.dukan.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderRepository : JpaRepository<Order, UUID>{
    fun existsByTransactionId(transactionId:UUID):Boolean
}