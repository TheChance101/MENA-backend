package net.thechance.chat.repository

import net.thechance.chat.entity.Contact
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContactRepository: JpaRepository<Contact, UUID>{

    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<Contact>
}