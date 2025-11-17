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
    fun updateDukan(
        dukanId: UUID,
        imageUrl:String?,
        status: WalletDukan.Status,
        activationStatus: WalletDukan.ActivationStatus?
    ) {
        val isUpdated = walletDukanRepository.updateDukan(
            dukanId = dukanId,
            imageUrl = imageUrl,
            status = status,
            activationStatus = activationStatus
        ) > 0

        if (!isUpdated) throw IllegalArgumentException("Dukan with id $dukanId not found")
    }
}