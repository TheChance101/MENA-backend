package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.api.dto.cart.CartPageResponse
import net.thechance.dukan.api.mapper.cart.toResponse
import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.CartItem
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.CartItemRepository
import net.thechance.dukan.repository.CartRepository
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.service.exception.CartNotFoundException
import net.thechance.dukan.service.exception.DukanNotFoundException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.service.exception.ProductNotInCartException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: DukanProductRepository,
    private val dukanRepository: DukanRepository,
    private val cartItemRepository: CartItemRepository,
) {
    @Transactional
    fun addOrUpdateItem(userId: UUID, dukanId: UUID, productId: UUID, quantity: Int): Cart {
        val cart = findOrCreateCart(userId, dukanId)
        val product = productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
        updateCartItems(cart, product, quantity)
        return cartRepository.save(cart)
    }

    private fun updateCartItems(cart: Cart, product: DukanProduct, quantity: Int) {
        val existingItem = cart.items.find { it.product.id == product.id }
        when {
            existingItem != null && quantity < 1 -> cart.items.remove(existingItem)
            existingItem != null -> existingItem.quantity = quantity
            quantity > 0 -> cart.items.add(CartItem(cart = cart, product = product, quantity = quantity))
        }
    }

    private fun fetchCart(userId: UUID, dukanId: UUID): Cart =
        cartRepository.findByUserIdAndDukanId(userId, dukanId)
            ?: throw CartNotFoundException()

    fun fetchCartWithPagedItems(userId: UUID, dukanId: UUID, pageable: Pageable): CartPageResponse {
        val cart = fetchCart(userId, dukanId)
        val itemsPage = cartItemRepository.findAllByCartId(cart.id, pageable)
        val itemsResponse = itemsPage.content.map { it.toResponse() }
        val totalPrice = cart.items.sumOf { it.product.price * it.quantity }

        return CartPageResponse(
            id = cart.id,
            dukanId = cart.dukan.id,
            totalPrice = totalPrice,
            items = itemsResponse,
            pagination = CartPageResponse.PageInfoResponse(
                page = itemsPage.number,
                size = itemsPage.size,
                totalPages = itemsPage.totalPages,
                totalItems = itemsPage.totalElements
            )
        )
    }

    @Transactional
    fun removeItem(userId: UUID, dukanId: UUID, productId: UUID) {
        val cart = fetchCart(userId, dukanId)
        val item = cart.items.find { it.product.id == productId } ?: throw ProductNotInCartException()
        cart.items.remove(item)
        cartRepository.save(cart)
    }

    private fun findOrCreateCart(userId: UUID, dukanId: UUID): Cart {
        val dukan = dukanRepository.findById(dukanId).orElseThrow { DukanNotFoundException() }
        return cartRepository.findByUserIdAndDukanId(userId, dukanId)
            ?: Cart(userId = userId, dukan = dukan).also { cartRepository.save(it) }
    }

}