package net.thechance.wallet.service

import jakarta.transaction.Transactional
import net.thechance.wallet.entity.WalletUser
import net.thechance.wallet.repository.WalletUserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class WalletUserService (
    private val userRepository: WalletUserRepository
) {
    fun getUserById(userId: UUID): WalletUser {
        return userRepository.findById(userId).orElseThrow {
            IllegalArgumentException("User with id $userId not found")
        }
    }

    fun getUserByPhoneNumber(phoneNumber: String): WalletUser {
        return userRepository.findByPhoneNumber(phoneNumber)
            ?: throw IllegalArgumentException("User with phone number $phoneNumber not found")
    }

    @Transactional
    fun updateUserStatus(userId: UUID, status: WalletUser.Status){
        val updatedRowsCount = userRepository.updateStatus(userId, status)
        if (updatedRowsCount == 0) throw IllegalArgumentException("User with id $userId not found")
    }

    fun getReferenceById(userId: UUID): WalletUser {
        return userRepository.getReferenceById(userId)
    }
}