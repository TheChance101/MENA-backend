package net.thechance.chat.repository

import net.thechance.chat.entity.ContactUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ContactUserRepository : JpaRepository<ContactUser, UUID> {
    @Query("SELECT c.phoneNumber FROM ContactUser c WHERE c.id = :id")
    fun findPhoneNumberById(@Param("id") id: UUID): String?
}