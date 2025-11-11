package net.thechance.wallet.service

import net.thechance.wallet.entity.WalletDukan
import net.thechance.wallet.repository.WalletDukanRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class WalletDukanService(
    private val walletDukanRepository: WalletDukanRepository
) {
    fun addDukan(dukanId: UUID, name: String, imageUrl: String?) {
        walletDukanRepository.save(
            WalletDukan(
                dukanId = dukanId,
                name = name,
                imageUrl = imageUrl,
            )
        )
    }

    fun removeDukan(dukanId: UUID) {
        walletDukanRepository.deleteById(dukanId)
    }


}