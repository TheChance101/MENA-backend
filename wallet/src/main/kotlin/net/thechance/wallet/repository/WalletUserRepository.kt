package net.thechance.wallet.repository

import net.thechance.wallet.entity.user.WalletUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface WalletUserRepository : JpaRepository<WalletUser, UUID>