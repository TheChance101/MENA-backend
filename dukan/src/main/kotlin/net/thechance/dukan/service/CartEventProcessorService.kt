package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.repository.DukanUserRepository
import net.thechance.dukan.repository.OrderRepository
import net.thechance.events.publisher.MenaEventPublisher
import net.thechance.events.wallet.TransactionCompletedEvent
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CartEventProcessorService(
    private val dukanService: DukanService,
    private val cartService: CartService,
    private val orderService: OrderService,
    private val soldProductsService: SoldProductsService,
    private val userRepository: DukanUserRepository,
    private val orderRepository: OrderRepository,
    private val eventPublisher: MenaEventPublisher
) {

    @Transactional
    fun processTransactionCompleted(event: TransactionCompletedEvent) {

        if (event.status != TransactionCompletedEvent.TransactionStatus.SUCCESS) return

        if (orderRepository.existsByTransactionId(event.transactionId)) {
            return
        }

        val dukan = dukanService.getDukanByOwnerId(event.receiverId)
        val cart = cartService.getActiveCart(event.senderId, dukan.id)
        val user = userRepository.findById(event.senderId).orElseThrow()
        val orderId = UUID.randomUUID()

        soldProductsService.addSoldProductsFromCart(cart)

        val order = orderService.createOrderFromCart(orderId, cart, user, dukan, event.transactionId)

        cartService.markCartAsPurchased(cart)

        val orderEvent = orderService.createOrderCreationEvent(order, dukan.ownerId)
        eventPublisher.publish(orderEvent)

        cartService.createNewCart(event.senderId, dukan.id)
    }
}