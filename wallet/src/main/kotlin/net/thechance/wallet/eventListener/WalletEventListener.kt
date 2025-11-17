package net.thechance.wallet.eventListener

import net.thechance.events.dukan.DukanCreationEvent
import net.thechance.events.dukan.DukanStatusChangedEvent
import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.events.wallet.InitiateTransactionEvent
import net.thechance.wallet.entity.WalletDukan
import net.thechance.wallet.eventListener.mapper.toEntityActivationStatus
import net.thechance.wallet.eventListener.mapper.toEntityDukan
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
    fun onUserStatusUpdated(event: UserStatusUpdatedEvent) {
        userService.updateUserStatus(
            userId = event.userId,
            status = event.status.toEntityStatus()
        )
    }

    @EventListener
    @Async
    fun onDukanCreated(event: DukanCreationEvent){
        dukanService.addDukan(event.toEntityDukan())
    }

    @EventListener
    @Async
    fun onDukanStatusChanged(event: DukanStatusChangedEvent){
        dukanService.updateDukanStatus(
            dukanId = event.dukanId,
            status = event.status.toEntityStatus()
        )
    }

    @EventListener
    @Async
    fun onDukanActivationStatusUpdated(event: DukanStatusChangedEvent){
        event.activationStatus?.let {
            dukanService.updateDukanActivationStatus(
                dukanId = event.dukanId,
                activationStatus = it.toEntityActivationStatus()
            )
        }
    }
}