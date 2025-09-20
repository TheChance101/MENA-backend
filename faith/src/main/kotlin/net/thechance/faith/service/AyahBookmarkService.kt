package net.thechance.faith.service

import net.thechance.faith.entity.AyahBookmark
import net.thechance.faith.repository.AyahBookmarkRepository
import org.springframework.stereotype.Service

@Service
class AyahBookmarkService(
    private val ayahBookmarkRepository: AyahBookmarkRepository
) {
    fun saveBookmark(ayahBookmark: AyahBookmark): AyahBookmark {
        return ayahBookmarkRepository.save(ayahBookmark)
    }
}
