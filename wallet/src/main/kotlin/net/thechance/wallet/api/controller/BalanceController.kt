package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.balance.BalanceResponse
import net.thechance.wallet.service.BalanceService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/wallet")
class BalanceController(
    private val balanceService: BalanceService
) {

    @GetMapping("/balance")
    fun getUserBalance(@AuthenticationPrincipal userId: UUID): ResponseEntity<BalanceResponse> {

        val balance = balanceService.getUserBalance(userId)

        val response = BalanceResponse(balance = balance)

        return ResponseEntity.ok(response)
    }
}