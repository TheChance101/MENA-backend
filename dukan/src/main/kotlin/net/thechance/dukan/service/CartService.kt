package net.thechance.dukan.service

import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.CartItem
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.entity.PendingOrder
import net.thechance.dukan.entity.PendingOrderItem
import net.thechance.dukan.repository.CartItemRepository
import net.thechance.dukan.repository.CartRepository
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.PendingOrderRepository
import net.thechance.dukan.service.exception.CartNotFoundException
import net.thechance.dukan.service.exception.DukanNotFoundException
import net.thechance.dukan.service.exception.ProductAlreadyInCartException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.service.exception.ProductNotInCartException
import net.thechance.dukan.service.model.AddOrUpdateCartItemParams
import net.thechance.dukan.service.model.CartCheckoutParams
import net.thechance.dukan.service.model.CartCheckoutPreview
import net.thechance.events.publisher.MenaEventPublisher
import net.thechance.events.wallet.InitiateTransactionEvent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: DukanProductRepository,
    private val cartItemRepository: CartItemRepository,
    private val dukanRepository: DukanRepository,
    private val pendingOrderRepository: PendingOrderRepository,
    private val eventPublisher: MenaEventPublisher
) {

    @Transactional
    fun addItem(params: AddOrUpdateCartItemParams): Cart {
        val cart = getOrCreateActiveCart(params.userId, params.dukanId)
        val product = getProduct(params.productId)

        if (cart.items.any { it.product.id == product.id })
            throw ProductAlreadyInCartException()

        val newItem = CartItem(
            product = product,
            quantity = params.quantity,
            cart = cart
        )

        cart.items.add(newItem)
        cart.calculateTotalPrice()
        return cartRepository.save(cart)
    }

    @Transactional
    fun updateItem(params: AddOrUpdateCartItemParams): Cart {
        val cart = getCartByUserAndDukan(params.userId, params.dukanId)
            ?: throw CartNotFoundException()

        val item = cart.items.find { it.product.id == params.productId }
            ?: throw ProductNotInCartException()

        item.quantity = params.quantity
        cart.calculateTotalPrice()
        return cartRepository.save(cart)
    }

    @Transactional
    fun removeItem(userId: UUID, dukanId: UUID, productId: UUID) {
        val cart = getCartOrThrow(userId, dukanId)
        val item = cart.items.find { it.product.id == productId }
            ?: throw ProductNotInCartException()
        cart.items.remove(item)
        if (cart.items.isEmpty()) cartRepository.delete(cart)
        else {
            cart.calculateTotalPrice()
            cartRepository.save(cart)
        }
    }

    @Transactional(readOnly = true)
    fun getCartOrThrow(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.findActiveCartByUserIdAndDukanId(userId, dukanId)
            ?: throw CartNotFoundException()
    }

    @Transactional(readOnly = true)
    fun getCartItems(userId: UUID, dukanId: UUID, pageable: Pageable): Page<CartItem> {
        val cart = getCartOrThrow(userId, dukanId)
        return cartItemRepository.findAllByCartId(cart.id, pageable)
    }

    @Transactional
    fun checkout(userId: UUID, checkoutParams: CartCheckoutParams): CartCheckoutPreview {
        val cart = cartRepository.findById(checkoutParams.cartId)
            .orElseThrow { CartNotFoundException() }

        isCartValid(cart, userId)

        val dukan = dukanRepository.findById(cart.dukanId)
            .orElseThrow { DukanNotFoundException() }

        val transactionId = UUID.randomUUID()

        createPendingOrder(transactionId, userId, cart, checkoutParams)

        createTransactionEvent(transactionId, cart, dukan)

        return CartCheckoutPreview(
            transactionId = transactionId,
            totalAmount = cart.totalPrice
        )
    }

    private fun isCartValid(cart: Cart, userId: UUID) {
        if (cart.userId != userId) {
            throw IllegalAccessException("Cart does not belong to this user")
        }

        if (cart.isOrderPurchased) {
            throw IllegalStateException("Cart is already purchased")
        }
    }

    private fun createTransactionEvent(
        transactionId: UUID,
        cart: Cart,
        dukan: Dukan
    ) {
        val transactionEvent = InitiateTransactionEvent(
            transactionId = transactionId,
            type = InitiateTransactionEvent.TransactionType.ONLINE_PURCHASE,
            senderId = cart.userId,
            receiverId = dukan.ownerId,
            amount = cart.totalPrice
        )

        eventPublisher.publish(transactionEvent)
    }

    private fun createPendingOrder(
        transactionId: UUID,
        userId: UUID,
        cart: Cart,
        checkoutParams: CartCheckoutParams
    ) {

        val pendingOrder = PendingOrder(
            transactionId = transactionId,
            userId = userId,
            dukanId = cart.dukanId,
            address = checkoutParams.address,
            longitude = checkoutParams.longitude,
            latitude = checkoutParams.latitude,
            totalPrice = cart.totalPrice,
        )

        cart.items.forEach { cartItem ->
            pendingOrder.items.add(
                PendingOrderItem(
                    pendingOrder = pendingOrder,
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity
                )
            )
        }

        pendingOrderRepository.save(pendingOrder)
    }

    private fun getCartByUserAndDukan(userId: UUID, dukanId: UUID): Cart? {
        return cartRepository.findActiveCartByUserIdAndDukanId(userId, dukanId)
    }
    private fun getOrCreateActiveCart(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.findActiveCartByUserIdAndDukanId(userId, dukanId)
            ?: createCart(userId, dukanId)
    }

    private fun createCart(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.saveAndFlush(Cart(userId = userId, dukanId = dukanId))
    }

    private fun getProduct(productId: UUID): DukanProduct =
        productRepository.findById(productId).orElseThrow { ProductNotFoundException() }
}