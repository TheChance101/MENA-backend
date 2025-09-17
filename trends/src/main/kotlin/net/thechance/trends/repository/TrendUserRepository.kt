package net.thechance.trends.repository

import net.thechance.trends.entity.TrendUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TrendUserRepository: JpaRepository<TrendUser, UUID>