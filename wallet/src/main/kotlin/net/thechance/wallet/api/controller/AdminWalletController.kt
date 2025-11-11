package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.balance.DepositRequest
import net.thechance.wallet.service.AdminWalletService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/wallet/admin")
class AdminWalletController(
    private val adminWalletService: AdminWalletService
) {
    @PostMapping("/balance/deposit")
    fun adminDeposit(
        @AuthenticationPrincipal adminId: UUID,
        @RequestBody request: DepositRequest
    ): ResponseEntity<Unit> {
        val response = adminWalletService.deposit(userId = adminId, request = request)
        return ResponseEntity.ok(response)
    }
}