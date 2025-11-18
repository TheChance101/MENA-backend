package net.thechance.wallet.repository

import net.thechance.wallet.entity.WalletDukan
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WalletDukanRepository: JpaRepository<WalletDukan, UUID>