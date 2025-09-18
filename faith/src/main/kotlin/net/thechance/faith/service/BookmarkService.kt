package net.thechance.faith.service

import net.thechance.faith.entity.AyahBookmark
import net.thechance.faith.repository.BookmarkRepository
import org.springframework.stereotype.Service

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository
) {
    fun saveBookmark(ayahBookmark: AyahBookmark): AyahBookmark {
        return bookmarkRepository.save(ayahBookmark)
    }
}
