package net.thechance.wallet.service

import jakarta.transaction.Transactional
import net.thechance.wallet.entity.WalletDukan
import net.thechance.wallet.repository.WalletDukanRepository
import net.thechance.wallet.repository.WalletUserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WalletDukanService(
    private val walletDukanRepository: WalletDukanRepository,
    private val walletUserRepository: WalletUserRepository
) {
    fun addDukan(dukan: WalletDukan, ownerId: UUID) {
        val savedDukan = walletDukanRepository.save(dukan)

        val user = walletUserRepository.findById(ownerId)
            .orElseThrow { IllegalArgumentException("User with id $ownerId not found") }

        walletUserRepository.save(
            user.copy(dukan = savedDukan)
        )
    }

    @Transactional
    fun updateDukan(dukan: WalletDukan) {
        if (!walletDukanRepository.existsById(dukan.dukanId)) {
            throw IllegalArgumentException("Dukan with id ${dukan.dukanId} not found")
        }
        walletDukanRepository.save(dukan)
    }
}