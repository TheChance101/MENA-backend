package net.thechance.wallet.api.controller

import net.thechance.events.identity.UserCreatedEvent
import net.thechance.wallet.api.dto.BalanceResponse
import net.thechance.wallet.service.WalletService
import org.springframework.context.event.EventListener
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/wallet")
class BalanceController(
    private val walletService: WalletService
) {

    @GetMapping("/balance")
    fun getUserBalance(@AuthenticationPrincipal userId: UUID): ResponseEntity<BalanceResponse> {

        val balance = walletService.getUserBalance(userId)

        val response = BalanceResponse(balance = balance)

        return ResponseEntity.ok(response)
    }
}

@Component
class WalletEventListener {

    @EventListener
    @Async
    fun onEvent(event: UserCreatedEvent) {
        println("testKarrar: User Created Event: $event")
    }
}