package net.thechance.faith.repository

import net.thechance.faith.entity.AyahBookmark
import org.springframework.data.jpa.repository.JpaRepository

interface AyahBookmarkRepository : JpaRepository<AyahBookmark, Int>