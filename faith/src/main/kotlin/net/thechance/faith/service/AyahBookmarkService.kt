package net.thechance.faith.service

import net.thechance.faith.entity.AyahBookmark
import net.thechance.faith.repository.AyahBookmarkRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class AyahBookmarkService(
    private val ayahBookmarkRepository: AyahBookmarkRepository
) {
    fun saveBookmark(ayahBookmark: AyahBookmark): AyahBookmark {
        return ayahBookmarkRepository.save(ayahBookmark)
    }

    fun getBookmarks(userId: UUID, pageable: Pageable): Page<AyahBookmark> {
        return ayahBookmarkRepository.findByUserId(ownerId = userId, pageable = pageable)
    }
}
