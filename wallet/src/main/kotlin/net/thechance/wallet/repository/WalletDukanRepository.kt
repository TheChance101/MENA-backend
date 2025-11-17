package net.thechance.wallet.repository

import net.thechance.wallet.entity.WalletDukan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

interface WalletDukanRepository: JpaRepository<WalletDukan, UUID>{
    @Modifying
    @Query("UPDATE WalletDukan w SET w.status = :status WHERE w.id = :dukanId")
    fun updateStatus(
        @Param("dukanId") dukanId: UUID,
        @Param("status") status: WalletDukan.Status
    ): Int

    @Modifying
    @Query("UPDATE WalletDukan w SET w.activationStatus = :activationStatus WHERE w.id = :dukanId")
    fun updateActivationStatus(
        @Param("dukanId") dukanId: UUID,
        @Param("activationStatus") activationStatus: WalletDukan.ActivationStatus
    ): Int
}