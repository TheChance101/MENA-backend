package net.thechance.dukan.repository

import net.thechance.dukan.entity.CartItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CartItemRepository : JpaRepository<CartItem, UUID> {
    fun findAllByCartId(cartId: UUID, pageable: Pageable): Page<CartItem>
}