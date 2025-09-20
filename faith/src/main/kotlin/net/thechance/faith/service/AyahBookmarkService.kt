package net.thechance.faith.service

import net.thechance.faith.api.controller.exception.AyahBookmarkNotFoundException
import net.thechance.faith.entity.AyahBookmark
import net.thechance.faith.repository.AyahBookmarkRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AyahBookmarkService(
    private val ayahBookmarkRepository: AyahBookmarkRepository
) {

    fun saveBookmark(ayahBookmark: AyahBookmark): AyahBookmark {
        return ayahBookmarkRepository.save(ayahBookmark)
    }

    fun deleteByIdAndUserId(id: Int, userId: UUID) {
        if (ayahBookmarkRepository.existsByUserIdAndId(userId, id).not())
            throw AyahBookmarkNotFoundException("Bookmark with id '$id' not found")
        ayahBookmarkRepository.deleteById(id)
    }
}
