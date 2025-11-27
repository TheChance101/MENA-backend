package net.thechance.dukan.eventListener

import net.thechance.dukan.service.CartEventProcessorService
import net.thechance.events.wallet.TransactionCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class CartEventListener(
    private val cartEventProcessorService: CartEventProcessorService
) {

    @EventListener
    @Async
    fun handle(event: TransactionCompletedEvent) {
        cartEventProcessorService.processTransactionCompleted(event)
    }
}