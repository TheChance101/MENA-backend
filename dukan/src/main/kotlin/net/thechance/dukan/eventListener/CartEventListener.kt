package net.thechance.dukan.eventListener

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanUser
import net.thechance.dukan.entity.Order
import net.thechance.dukan.entity.OrderItem
import net.thechance.dukan.entity.SoldProduct
import net.thechance.dukan.repository.CartRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.DukanUserRepository
import net.thechance.dukan.repository.OrderRepository
import net.thechance.dukan.repository.SoldProductRepository
import net.thechance.dukan.service.exception.CartNotFoundException
import net.thechance.dukan.service.exception.DukanNotFoundException
import net.thechance.events.publisher.MenaEventPublisher
import net.thechance.events.wallet.TransactionCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Component
class CartEventListener(
    private val dukanRepository: DukanRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val soldProductsRepository: SoldProductRepository,
    private val dukanUserRepository: DukanUserRepository,
    private val eventPublisher: MenaEventPublisher
) {

    @EventListener
    @Transactional
    @Async
    fun handle(event: TransactionCompletedEvent) {
        if (event.status != TransactionCompletedEvent.TransactionStatus.SUCCESS) return

        val dukan = findDukan(event.receiverId)
        val cart = findActiveCart(event.senderId, dukan.id)
        val user = dukanUserRepository.findById(event.senderId).orElseThrow()

        processSoldProducts(cart)
        createOrderFromCart(cart, user, dukan, event.transactionId)
        finalizeCartPurchase(cart)

        createNewCart(event.senderId, dukan.id)
    }

    private fun findDukan(ownerId: UUID): Dukan {
        return dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()
    }

    private fun findActiveCart(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.findByUserIdAndDukanIdAndIsOrderPurchasedFalse(userId, dukanId)
            ?: throw CartNotFoundException()
    }

    private fun processSoldProducts(cart: Cart) {
        cart.items.forEach { item ->
            upsertSoldProduct(
                productId = item.product.id,
                dukanId = cart.dukanId,
                quantity = item.quantity
            )
        }
    }

    private fun upsertSoldProduct(productId: UUID, dukanId: UUID, quantity: Int) {
        val existing = soldProductsRepository.findByProductIdAndDukanId(productId, dukanId)

        if (existing != null) {
            updateSoldProductQuantity(existing, quantity)
        } else {
            createNewSoldProduct(productId, dukanId, quantity)
        }
    }

    private fun updateSoldProductQuantity(existing: SoldProduct, addQuantity: Int) {
        val updated = existing.copy(quantity = existing.quantity + addQuantity)
        soldProductsRepository.save(updated)
    }

    private fun createNewSoldProduct(productId: UUID, dukanId: UUID, quantity: Int) {
        val newSold = SoldProduct(
            productId = productId,
            dukanId = dukanId,
            quantity = quantity
        )
        soldProductsRepository.save(newSold)
    }

    private fun createOrderFromCart(
        cart: Cart,
        user: DukanUser,
        dukan: Dukan,
        transactionId: UUID
    ) {

        val discount = cart.getDiscountPercentage()

        val order = createOrder(cart, transactionId, discount, user, dukan)

        orderRepository.save(order)
    }

    private fun createOrder(
        cart: Cart,
        transactionId: UUID,
        discount: BigDecimal,
        user: DukanUser,
        dukan: Dukan
    ): Order {
        val order = Order(
            userId = cart.userId,
            dukanId = cart.dukanId,
            transactionId = transactionId,
            totalBeforeDiscount = cart.price.base,
            discountPercentage = discount,
            totalAfterDiscount = cart.price.final,
            platformFees = cart.platformFees,
            customerName = user.userName,
            customerPhone = user.phoneNumber,
            customerImage = user.imageUrl ?: "",
            deliveryAddress = "",
            deliveryLat = 0.0,
            deliveryLng = 0.0,
            dukanLongitude = dukan.longitude,
            dukanLatitude = dukan.latitude
        )
        cart.items.forEach { item ->
            order.items.add(
                OrderItem(
                    order = order,
                    productId = item.product.id,
                    productName = item.product.name,
                    productImage = item.product.imageUrls.first(),
                    priceBeforeDiscount = item.product.price.base,
                    quantity = item.quantity,
                    priceAfterDiscount = item.product.price.final
                )
            )
        }
        return order
    }

    private fun finalizeCartPurchase(cart: Cart) {
        cart.isOrderPurchased = true
        cart.updatedAt = Instant.now()
        cartRepository.save(cart)
    }

    private fun createNewCart(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.saveAndFlush(Cart(userId = userId, dukanId = dukanId))
    }
}