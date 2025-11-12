package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.balance.AdminDepositRequest
import net.thechance.wallet.service.DepositService
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
    private val adminWalletService: DepositService
) {
    @PostMapping("/deposit")
    fun depositMoneyToUser(
        @AuthenticationPrincipal adminId: UUID,
        @RequestBody request: AdminDepositRequest
    ): ResponseEntity<Unit> {
        adminWalletService.depositMoney(
            adminId = adminId,
            receiverPhoneNumber = request.phoneNumber,
            amount = request.amount
        )
        return ResponseEntity.ok().build()
    }
}