package net.thechance.dukan.service

import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.CartItem
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.CartItemRepository
import net.thechance.dukan.repository.CartRepository
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.service.exception.CartNotFoundException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.service.exception.ProductNotInCartException
import net.thechance.dukan.service.model.AddOrUpdateCartItemParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: DukanProductRepository,
    private val cartItemRepository: CartItemRepository,
) {

    @Transactional
    fun addOrUpdateItem(params: AddOrUpdateCartItemParams): Cart {
        val cart = getOrCreateCart(params.userId, params.dukanId)
        val product = getProduct(params.productId)
        val item = cart.items.find { it.product.id == product.id }

        if (item != null) item.quantity = params.quantity
        else cart.items.add(CartItem(product = product, quantity = params.quantity, cart = cart))

        cart.updatedAt = Instant.now()
        return cartRepository.save(cart)
    }

    @Transactional
    fun removeItem(userId: UUID, dukanId: UUID, productId: UUID) {
        val cart = getCart(userId, dukanId)
        val item = cart.items.find { it.product.id == productId } ?: throw ProductNotInCartException()

        cart.items.remove(item)
        if (cart.items.isEmpty()) cartRepository.delete(cart)
        else {
            cart.updatedAt = Instant.now()
            cartRepository.save(cart)
        }
    }

    @Transactional(readOnly = true)
    fun getCart(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.findByUserIdAndDukanIdWithItemsAndProducts(userId, dukanId)
            ?: throw CartNotFoundException()
    }

    @Transactional(readOnly = true)
    fun getCartItems(userId: UUID, dukanId: UUID, pageable: Pageable): Page<CartItem> {
        val cart = getCart(userId, dukanId)
        return cartItemRepository.findAllByCartId(cart.id, pageable)
    }

    private fun getOrCreateCart(userId: UUID, dukanId: UUID): Cart {
        return synchronized(userId.toString() + dukanId.toString()) {
            cartRepository.findByUserIdAndDukanIdWithItemsAndProducts(userId, dukanId)
                ?: cartRepository.saveAndFlush(Cart(userId = userId, dukanId = dukanId))
        }
    }

    private fun getProduct(productId: UUID): DukanProduct =
        productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
}