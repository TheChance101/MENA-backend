package net.thechance.faith.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.faith.entity.Bookmark
import net.thechance.faith.repository.BookmarkRepository
import org.junit.Test
import java.time.Instant
import java.util.*

class BookmarkServiceTest {
    private val bookmarkRepository = mockk<BookmarkRepository>()
    private val bookmarkService = BookmarkService(bookmarkRepository)

    @Test
    fun `saveBookmark should save bookmark and return saved entity when it called`() {
        every { bookmarkRepository.save(bookmark) } returns bookmark

        val result = bookmarkService.saveBookmark(bookmark)

        assertThat(result).isEqualTo(bookmark)
    }

    private companion object {
        val bookmark = Bookmark(
            id = 1,
            userId = UUID.randomUUID(),
            surahId = 1,
            ayahNumber = 2,
            createdAt = Instant.now()
        )
    }
}

