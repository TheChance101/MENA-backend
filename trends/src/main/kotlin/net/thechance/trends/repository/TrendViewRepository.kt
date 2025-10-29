package net.thechance.trends.repository

import net.thechance.trends.entity.TrendView
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TrendViewRepository : JpaRepository<TrendView, UUID>