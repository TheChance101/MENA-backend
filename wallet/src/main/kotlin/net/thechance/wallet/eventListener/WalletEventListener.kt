package net.thechance.wallet.eventListener

import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserDeletedEvent
import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import net.thechance.events.wallet.InitiateTransactionEvent
import net.thechance.wallet.eventListener.mapper.toEntityStatus
import net.thechance.wallet.eventListener.mapper.toInitiateTransactionParams
import net.thechance.wallet.eventListener.mapper.toWalletUserEntity
import net.thechance.wallet.service.TransactionService
import net.thechance.wallet.service.WalletUserService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WalletEventListener(
    private val transactionService: TransactionService,
    private val userService: WalletUserService
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

    @EventListener
    @Async
    fun onNewUserAdded(event: UserCreatedEvent) {
        userService.addUser(event.toWalletUserEntity())
    }

    @EventListener
    @Async
    fun onUserDeleted(event: UserDeletedEvent) {
        userService.deleteUser(userId = event.id)
    }

    @EventListener
    @Async
    fun onUserUpdated(event: UserUpdatedEvent) {
        userService.updateUser(event.toWalletUserEntity())
    }
}