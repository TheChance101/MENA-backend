package net.thechance.faith.repository

import net.thechance.faith.entity.AyahBookmark
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkRepository : JpaRepository<AyahBookmark, Int>