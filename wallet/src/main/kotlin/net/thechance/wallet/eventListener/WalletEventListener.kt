package net.thechance.wallet.eventListener

import net.thechance.events.dukan.DukanStatusChangedEvent
import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.events.wallet.InitiateTransactionEvent
import net.thechance.wallet.eventListener.mapper.toEntityStatus
import net.thechance.wallet.eventListener.mapper.toInitiateTransactionParams
import net.thechance.wallet.service.TransactionService
import net.thechance.wallet.service.WalletDukanService
import net.thechance.wallet.service.WalletUserService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WalletEventListener(
    private val transactionService: TransactionService,
    private val userService: WalletUserService,
    private val dukanService: WalletDukanService
) {

    @EventListener
    @Async
    fun onInitiateTransaction(event: InitiateTransactionEvent) {
        transactionService.initiateTransaction(event.toInitiateTransactionParams())
    }

    @EventListener
    @Async
    fun onDukanStatusUpdated(event: DukanStatusChangedEvent) {
        if (event.status == DukanStatusChangedEvent.DukanEventStatus.APPROVED &&
            event.activationStatus == DukanStatusChangedEvent.DukanEventActivationStatus.ACTIVATED
        ) {
            dukanService.addDukan(
                dukanId = event.dukanId,
                name = event.name,
                imageUrl = event.imageUrl
            )
        }

        else if(event.activationStatus == DukanStatusChangedEvent.DukanEventActivationStatus.DEACTIVATED){
            dukanService.removeDukan(dukanId = event.dukanId)
        }
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