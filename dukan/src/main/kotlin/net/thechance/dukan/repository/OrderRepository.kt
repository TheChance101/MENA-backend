package net.thechance.dukan.repository

import net.thechance.dukan.entity.Order
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderRepository : JpaRepository<Order, UUID>