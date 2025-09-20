package net.thechance.chat.repository

import net.thechance.chat.entity.Contact
import net.thechance.chat.service.model.ContactModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ContactRepository: JpaRepository<Contact, UUID>{

    @Query(
        "SELECT new net.thechance.chat.service.model.ContactModel(" +
                "c.id, " +
                "c.firstName, " +
                "c.lastName, " +
                "c.phoneNumber, " +
                "CASE WHEN u IS NOT NULL THEN true ELSE false END, " +
                "CASE WHEN u IS NOT NULL THEN u.imageUrl ELSE null END) "+
                "FROM Contact c " +
                "LEFT JOIN ContactUser u ON u.phoneNumber = c.phoneNumber " +
                "WHERE c.contactOwnerId = :contactOwnerId"
    )
    fun findAllContactModelsByContactOwnerId(
        @Param("contactOwnerId") contactOwnerId: UUID,
        pageable: Pageable?
    ): Page<ContactModel>
}