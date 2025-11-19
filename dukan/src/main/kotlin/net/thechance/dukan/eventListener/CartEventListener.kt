package net.thechance.dukan.eventListener

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.*
import net.thechance.dukan.repository.*
import net.thechance.dukan.service.exception.CartNotFoundException
import net.thechance.dukan.service.exception.DukanNotFoundException
import net.thechance.events.dukan.OrderCreationEvent
import net.thechance.events.publisher.MenaEventPublisher
import net.thechance.events.wallet.TransactionCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
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
        if (orderRepository.existsByTransactionId(event.transactionId)) {
            return // event already processed
        }

        val dukan = findDukan(event.receiverId)
        val cart = findActiveCart(event.senderId, dukan.id)
        val user = dukanUserRepository.findById(event.senderId).orElseThrow()

        processSoldProducts(cart)
        val order = createOrderFromCart(cart, user, dukan, event.transactionId)
        finalizeCartPurchase(cart)
        val orderCreationEvent = createOrderEvent(order,dukan.ownerId)
        createNewCart(event.senderId, dukan.id).also {
            eventPublisher.publish(orderCreationEvent)
        }
    }

    private fun createOrderEvent(order: Order, ownerId: UUID): OrderCreationEvent {
        return OrderCreationEvent(
            orderId = order.id,
            userId = order.userId,
            dukanId = order.dukanId,
            dukanOwnerId = ownerId,
            totalProducts = order.items.size,
            totalPrice =order.totalAfterDiscount,
            deliverToAddress = order.deliveryAddress
        )
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
    ): Order {

        val order = createOrder(cart, transactionId, user, dukan)

        orderRepository.save(order)
        return order
    }

    private fun createOrder(
        cart: Cart,
        transactionId: UUID,
        user: DukanUser,
        dukan: Dukan
    ): Order {
        val order = Order(
            userId = cart.userId,
            dukanId = cart.dukanId,
            transactionId = transactionId,
            totalBeforeDiscount = cart.price.base,
            discountPercentage = cart.getDiscountPercentage(),
            totalAfterDiscount = cart.price.final,
            platformFees = cart.platformFees,
            customerName = user.userName,
            customerPhone = user.phoneNumber,
            customerImage = user.imageUrl ?: "",
            deliveryAddress = user.address,
            deliveryLat = user.latitude,
            deliveryLng = user.longitude,
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