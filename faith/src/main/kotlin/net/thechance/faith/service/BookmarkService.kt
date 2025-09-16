package net.thechance.faith.service

import net.thechance.faith.entity.Bookmark
import net.thechance.faith.repository.BookmarkRepository
import org.springframework.stereotype.Service

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository
) {
    fun saveBookmark(bookmark: Bookmark): Bookmark {
        return bookmarkRepository.save(bookmark)
    }
}
