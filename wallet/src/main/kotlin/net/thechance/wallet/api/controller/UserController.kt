package net.thechance.wallet.api.controller

import net.thechance.wallet.api.dto.user.UserDetailsResponse
import net.thechance.wallet.api.dto.user.toUserDetailsResponse
import net.thechance.wallet.repository.WalletUserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/wallet/user")
class UserController(
    private val walletUserRepository: WalletUserRepository
) {
    @GetMapping("/details")
    fun getUserDetails(
        @AuthenticationPrincipal currentUserId: UUID,
        @RequestParam userId: UUID
    ): ResponseEntity<UserDetailsResponse> {
        val user = walletUserRepository.findById(userId).get()
        return ResponseEntity.ok(user.toUserDetailsResponse())
    }
}