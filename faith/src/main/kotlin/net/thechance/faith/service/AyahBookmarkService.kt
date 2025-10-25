package net.thechance.faith.service

import jakarta.transaction.Transactional
import net.thechance.faith.entity.AyahBookmark
import net.thechance.faith.exception.AyahBookmarkNotFoundException
import net.thechance.faith.repository.AyahBookmarkRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class AyahBookmarkService(
    private val ayahBookmarkRepository: AyahBookmarkRepository
) {

    @Transactional
    fun saveBookmark(ayahBookmark: AyahBookmark): AyahBookmark {
        return ayahBookmarkRepository.save(ayahBookmark)
    }

    @Transactional
    fun deleteByIdAndUserId(id: Int, userId: UUID) {
        val deleted = ayahBookmarkRepository.deleteByUserIdAndId(userId, id) > 0
        if (deleted.not()) throw AyahBookmarkNotFoundException("Bookmark with id '$id' not found")
    }

    fun getBookmarks(userId: UUID, pageable: Pageable): Page<AyahBookmark> {
        return ayahBookmarkRepository.findByUserId(ownerId = userId, pageable = pageable)
    }
}
