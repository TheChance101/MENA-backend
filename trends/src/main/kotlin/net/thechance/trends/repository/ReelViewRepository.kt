package net.thechance.trends.repository

import net.thechance.trends.entity.ReelView
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReelViewRepository : JpaRepository<ReelView, UUID>