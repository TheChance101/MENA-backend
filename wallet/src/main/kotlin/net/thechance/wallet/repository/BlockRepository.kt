package net.thechance.wallet.repository

import net.thechance.wallet.entity.Block
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.*

interface BlockRepository : JpaRepository<Block, UUID>{
    fun findBlockByTimestampBetween(start: LocalDateTime, end: LocalDateTime): Block?
    fun findTopByOrderByTimestampDesc(): Block?
}