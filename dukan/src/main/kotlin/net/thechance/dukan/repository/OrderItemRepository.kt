package net.thechance.dukan.repository

import net.thechance.dukan.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderItemRepository : JpaRepository<OrderItem, UUID>