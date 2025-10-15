package net.thechance.wallet.eventListener

import net.thechance.events.wallet.TransactionInitiatedEvent
import net.thechance.wallet.service.TransactionService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class WalletEventListener(
    private val transactionService: TransactionService
) {

    @EventListener
    fun onInitiateTransaction(event: TransactionInitiatedEvent) {
        transactionService.initiateTransaction(event.toInitiateTransactionParams())
    }
}