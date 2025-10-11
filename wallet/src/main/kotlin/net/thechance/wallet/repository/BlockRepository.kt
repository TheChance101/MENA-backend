package net.thechance.wallet.repository

import net.thechance.wallet.entity.Block
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface BlockRepository : JpaRepository<Block, UUID>{
    fun findTopByOrderByTimestampDesc(): Block?
}