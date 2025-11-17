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
    fun addDukan(dukan: WalletDukan){
        walletDukanRepository.save(dukan)
    }

    @Transactional
    fun updateDukanStatus(dukanId: UUID, status: WalletDukan.Status){
        val isUpdated = walletDukanRepository.updateStatus(dukanId, status) > 0
        if (!isUpdated) throw IllegalArgumentException("Dukan with id $dukanId not found")
    }

    @Transactional
    fun updateDukanActivationStatus(dukanId: UUID, activationStatus: WalletDukan.ActivationStatus){
        val isUpdated = walletDukanRepository.updateActivationStatus(dukanId, activationStatus) > 0
        if (!isUpdated) throw IllegalArgumentException("Dukan with id $dukanId not found")
    }
}