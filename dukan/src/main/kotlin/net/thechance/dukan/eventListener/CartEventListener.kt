package net.thechance.dukan.eventListener

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Cart
import net.thechance.dukan.repository.CartRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.service.exception.CartNotFoundException
import net.thechance.dukan.service.exception.DukanNotFoundException
import net.thechance.events.publisher.MenaEventPublisher
import net.thechance.events.wallet.TransactionCompletedEvent
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class CartEventListener(
    private val dukanRepository: DukanRepository,
    private val cartRepository: CartRepository,
    private val eventPublisher: MenaEventPublisher
) {

    @Transactional
    fun handle(
        transactionCompletedEvent: TransactionCompletedEvent
    ) {
        val dukan = dukanRepository.findByOwnerId(transactionCompletedEvent.receiverId)
            ?: throw DukanNotFoundException()

        if (transactionCompletedEvent.status == TransactionCompletedEvent.TransactionStatus.SUCCESS) {
            val cart = cartRepository.findByUserIdAndDukanIdAndIsOrderPurchasedFalse(
                    transactionCompletedEvent.senderId,
                    dukan.id
                ) ?: throw CartNotFoundException()


            cart.isOrderPurchased = true
            cart.updatedAt = Instant.now()
            cartRepository.save(cart)

            createCart(transactionCompletedEvent.senderId, dukan.id)
        }
    }

    private fun createCart(userId: UUID, dukanId: UUID): Cart {
        return cartRepository.saveAndFlush(Cart(userId = userId, dukanId = dukanId))
    }
}