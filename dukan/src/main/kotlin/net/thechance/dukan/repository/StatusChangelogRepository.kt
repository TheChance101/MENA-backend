package net.thechance.dukan.repository

import net.thechance.dukan.entity.StatusChangelog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StatusChangelogRepository: JpaRepository<StatusChangelog, UUID>