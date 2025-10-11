package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.user.UserDetailsResponse
import net.thechance.wallet.api.dto.user.toUserDetailsResponse
import net.thechance.wallet.service.WalletUserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/wallet/user")
class UserController(
    private val walletUserService: WalletUserService,
) {
    @GetMapping("/details")
    fun getUserDetails(
        @RequestParam currentUserId: UUID,
        @RequestParam userId: UUID
    ): ResponseEntity<UserDetailsResponse> {
        val user = walletUserService.getUserById(userId)
        return ResponseEntity.ok(user.toUserDetailsResponse())
    }
}