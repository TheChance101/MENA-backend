package net.thechance.wallet.repository

import net.thechance.wallet.entity.WalletDukan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface WalletDukanRepository: JpaRepository<WalletDukan, UUID>{
    @Modifying
    @Query("""
    UPDATE WalletDukan w 
    SET 
        w.status = :status,
        w.activationStatus = :activationStatus
    WHERE w.id = :dukanId
    """)
    fun updateStatuses(
        @Param("dukanId") dukanId: UUID,
        @Param("status") status: WalletDukan.Status,
        @Param("activationStatus") activationStatus: WalletDukan.ActivationStatus?
    ): Int
}