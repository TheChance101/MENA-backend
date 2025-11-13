package net.thechance.identity.repository

import net.thechance.identity.entity.PrivacyAndPolicy
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PrivacyAndPolicyRepository : JpaRepository<PrivacyAndPolicy, UUID> {
    fun getAllByLanguageOrderByShowOrder(language: String): List<PrivacyAndPolicy>
}
