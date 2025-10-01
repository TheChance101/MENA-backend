package net.thechance.wallet.eventListener

import net.thechance.events.identity.UserCreatedEvent
import net.thechance.wallet.service.WalletService
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WalletEventListener(
    val walletService: WalletService
) {

    @EventListener
    @Async
    fun onEvent(event: UserCreatedEvent) {
        println("testKarrar: User Created Event: $event")
        walletService.doSomething()
    }
}