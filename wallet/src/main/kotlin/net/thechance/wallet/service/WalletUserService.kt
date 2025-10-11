package net.thechance.wallet.service

import net.thechance.wallet.entity.user.WalletUser
import net.thechance.wallet.repository.WalletUserRepository
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrElse

@Service
class WalletUserService (
    private val walletUserRepository: WalletUserRepository
){
    fun getUserById(userId: UUID): WalletUser =
        walletUserRepository.findById(userId)
            .getOrElse { throw IllegalArgumentException("User not found") }
}