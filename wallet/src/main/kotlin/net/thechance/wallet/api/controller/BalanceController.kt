package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.balance.BalanceResponse
import net.thechance.wallet.api.dto.balance.DepositRequest
import net.thechance.wallet.api.dto.balance.toBalanceResponse
import net.thechance.wallet.service.BalanceService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
        val response = balanceService.getUserBalance(userId).toBalanceResponse()

        return ResponseEntity.ok(response)
    }

    @PostMapping("/admin/balance/deposit")
    fun adminDeposit(
        @AuthenticationPrincipal adminId: UUID,
        @RequestBody request: DepositRequest
    ): ResponseEntity<Unit> {
        val response = balanceService.deposit(userId = adminId, request = request)
        return ResponseEntity.ok(response)
    }
}