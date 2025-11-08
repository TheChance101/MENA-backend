package net.thechance.wallet.repository

import net.thechance.wallet.entity.WalletUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface WalletUserRepository : JpaRepository<WalletUser, UUID> {

    @Modifying
    @Query("UPDATE WalletUser u SET u.status = :status WHERE u.userId = :userId")
    fun updateStatus(
        @Param("userId") userId: UUID,
        @Param("status") status: WalletUser.Status
    ): Int
}