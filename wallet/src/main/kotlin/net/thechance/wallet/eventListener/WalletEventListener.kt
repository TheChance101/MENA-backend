package net.thechance.wallet.eventListener

import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.events.wallet.InitiateTransactionEvent
import net.thechance.wallet.eventListener.mapper.toEntityStatus
import net.thechance.wallet.eventListener.mapper.toInitiateTransactionParams
import net.thechance.wallet.service.TransactionService
import net.thechance.wallet.service.WalletUserService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WalletEventListener(
    private val transactionService: TransactionService,
    private val userService: WalletUserService,
) {

    @EventListener
    @Async
    fun onInitiateTransaction(event: InitiateTransactionEvent) {
        transactionService.initiateTransaction(event.toInitiateTransactionParams())
    }

    @EventListener
    @Async
    fun onUserStatusUpdated(event: UserStatusUpdatedEvent) {
        userService.updateUserStatus(
            userId = event.userId,
            status = event.status.toEntityStatus()
        )
    }
}