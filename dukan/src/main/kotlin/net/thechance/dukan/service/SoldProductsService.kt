package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.SoldProduct
import net.thechance.dukan.repository.SoldProductRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SoldProductsService(
    private val soldProductRepository: SoldProductRepository
) {

    @Transactional
    fun addSoldProductsFromCart(cart: Cart) {
        cart.items.forEach { item ->
            upsertSoldProduct(
                productId = item.product.id,
                dukanId = cart.dukanId,
                quantity = item.quantity
            )
        }
    }

    private fun upsertSoldProduct(productId: UUID, dukanId: UUID, quantity: Int) {
        val existing = soldProductRepository.findByProductIdAndDukanId(productId, dukanId)

        if (existing != null) {
            soldProductRepository.save(existing.copy(quantity = existing.quantity + quantity))
        } else {
            soldProductRepository.save(
                SoldProduct(productId = productId, dukanId = dukanId, quantity = quantity)
            )
        }
    }
}