package net.thechance.faith.repository

import net.thechance.faith.entity.AyahBookmark
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AyahBookmarkRepository : JpaRepository<AyahBookmark, Int> {
    fun existsByUserIdAndId(userId: UUID, id: Int): Boolean
}
