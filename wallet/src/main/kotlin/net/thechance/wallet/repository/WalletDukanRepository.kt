package net.thechance.wallet.repository

import net.thechance.wallet.entity.WalletDukan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface WalletDukanRepository: JpaRepository<WalletDukan, UUID>