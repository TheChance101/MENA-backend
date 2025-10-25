package net.thechance.wallet.eventListener

import net.thechance.events.wallet.InitiateTransactionEvent
import net.thechance.wallet.eventListener.mapper.toInitiateTransactionParams
import net.thechance.wallet.service.TransactionService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class WalletEventListener(
    private val transactionService: TransactionService
) {

    @EventListener
    fun onInitiateTransaction(event: InitiateTransactionEvent) {
        val id = transactionService.initiateTransaction(event.toInitiateTransactionParams()).id
        event.response?.complete(id)
    }
}