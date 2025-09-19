package net.thechance.faith.repository

import net.thechance.faith.entity.AyahBookmark
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AyahBookmarkRepository : JpaRepository<AyahBookmark, Int> {
    fun findByUserId(ownerId: UUID, pageable: Pageable): Page<AyahBookmark>
}