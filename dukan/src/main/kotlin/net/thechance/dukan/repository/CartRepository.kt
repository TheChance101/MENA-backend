package net.thechance.dukan.repository

import net.thechance.dukan.entity.Cart
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CartRepository : JpaRepository<Cart, UUID> {
    fun findByUserIdAndDukanId(userId: UUID, dukanId: UUID): Cart?
}