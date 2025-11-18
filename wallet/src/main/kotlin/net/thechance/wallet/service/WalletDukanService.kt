package net.thechance.wallet.service

import jakarta.transaction.Transactional
import net.thechance.wallet.entity.WalletDukan
import net.thechance.wallet.repository.WalletDukanRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WalletDukanService(
    private val walletDukanRepository: WalletDukanRepository
) {
    fun addDukan(dukan: WalletDukan) {
        walletDukanRepository.save(dukan)
    }

    @Transactional
    fun updateDukan(dukan: WalletDukan) {
        if (!walletDukanRepository.existsById(dukan.dukanId)) {
            throw IllegalArgumentException("Dukan with id ${dukan.dukanId} not found")
        }
        walletDukanRepository.save(dukan)
    }
}