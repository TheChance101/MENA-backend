package net.thechance.faith.repository

import net.thechance.faith.entity.AyahBookmark
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface AyahBookmarkRepository : JpaRepository<AyahBookmark, Int> {
    fun findByUserId(ownerId: UUID, pageable: Pageable): Page<AyahBookmark>
    fun deleteByUserIdAndId(userId: UUID, id: Int): Int

    @Modifying
    @Query(
        value = """
                  INSERT INTO faith.ayah_bookmarks (user_id, surah_id, ayah_number, created_at)
                  VALUES (:#{#ayahBookmark.userId}, :#{#ayahBookmark.surahId}, :#{#ayahBookmark.ayahNumber}, :#{#ayahBookmark.createdAt})
                  ON CONFLICT (user_id, surah_id, ayah_number) 
                  DO UPDATE SET created_at = EXCLUDED.created_at
                """,
        nativeQuery = true
    )
    fun upsert(ayahBookmark: AyahBookmark): Int

}
