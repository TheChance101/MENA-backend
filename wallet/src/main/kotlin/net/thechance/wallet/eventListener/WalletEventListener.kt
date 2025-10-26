package net.thechance.wallet.eventListener

import net.thechance.events.wallet.InitiateTransactionEvent
import net.thechance.wallet.eventListener.mapper.toInitiateTransactionParams
import net.thechance.wallet.service.TransactionService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class WalletEventListener(
    private val transactionService: TransactionService
) {

    @EventListener
    fun onInitiateTransaction(event: InitiateTransactionEvent) {
        transactionService.initiateTransaction(event.toInitiateTransactionParams())
    }
}