package net.thechance.faith.repository

import net.thechance.faith.entity.Bookmark
import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkRepository : JpaRepository<Bookmark, Int>