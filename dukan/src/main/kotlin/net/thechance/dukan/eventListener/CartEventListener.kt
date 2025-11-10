package net.thechance.dukan.eventListener

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.Order
import net.thechance.dukan.entity.PendingOrder
import net.thechance.dukan.entity.SoldProduct
import net.thechance.dukan.repository.CartRepository
import net.thechance.dukan.repository.OrderRepository
import net.thechance.dukan.repository.PendingOrderRepository
import net.thechance.dukan.repository.SoldProductRepository
import net.thechance.dukan.service.exception.CartNotFoundException
import net.thechance.events.publisher.MenaEventPublisher
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class CartEventListener(
    private val pendingOrderRepository: PendingOrderRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val soldProductRepository: SoldProductRepository,
    private val eventPublisher: MenaEventPublisher
) {

    // TODO: inject with TransactionCompletedEvent
    @Transactional
    fun handle() {

        val pendingOrder = pendingOrderRepository.findByTransactionId(
            UUID.randomUUID() // TODO: Replace with the transactionId comingg from TransactionCompletedEvent
        ) ?: return

        createOrder(pendingOrder)
        createSoldProducts(pendingOrder)

        val cart =
            cartRepository.findByUserIdAndDukanIdAndIsOrderPurchasedFalse(pendingOrder.userId, pendingOrder.dukanId)
                ?: throw CartNotFoundException()

        cart.isOrderPurchased = true
        cart.updatedAt = Instant.now()
        cartRepository.save(cart)

        createCart(pendingOrder.userId, pendingOrder.dukanId)
    }

    private fun createSoldProducts(pending: PendingOrder) {
        pending.items.forEach { product ->
            soldProductRepository.save(
                SoldProduct(
                    productId = product.productId,
                    dukanId = pending.dukanId,
                    quantity = product.quantity
                )
            )
        }

        pendingOrderRepository.delete(pending)
    }

    private fun createOrder(pending: PendingOrder) {
        val order = Order(
            userId = pending.userId,
            dukanId = pending.dukanId,
            address = pending.address,
            longitude = pending.longitude,
            latitude = pending.latitude,
            totalPrice = pending.totalPrice,
            totalProducts = pending.items.sumOf { it.quantity },
            createdAt = Instant.now()
        )

        orderRepository.save(order)
    }

    private fun createCart(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.saveAndFlush(Cart(userId = userId, dukanId = dukanId))
    }
}