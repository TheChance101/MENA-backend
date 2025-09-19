package net.thechance.chat.repository

import net.thechance.chat.entity.Contact
import net.thechance.chat.service.ContactModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ContactRepository: JpaRepository<Contact, UUID>{

    fun findAllByContactOwnerId(userId: UUID, pageable: Pageable): Page<Contact>

    @Query(
        "SELECT new net.thechance.chat.service.ContactModel(" +
                "c.id, " +
                "c.firstName, " +
                "c.lastName, " +
                "c.phoneNumber, " +
                "CASE WHEN u IS NOT NULL THEN true ELSE false END, " +
                "u.imageUrl) " +
                "FROM Contact c " +
                "LEFT JOIN User u ON u.phoneNumber = c.phoneNumber " +
                "WHERE c.ownerUser.id = :ownerUserId"
    )
    fun findAllContactResponsesByOwnerUserId(
        @Param("ownerUserId") ownerUserId: UUID,
        pageable: Pageable?
    ): Page<ContactModel>


    fun findAllByContactOwnerIdAndPhoneNumberIn(contactOwnerId: UUID, phoneNumbers: List<String>): List<Contact>
    fun deleteAllByContactOwnerIdAndPhoneNumberNotIn(contactOwnerId: UUID, requestedPhoneNumbers: List<String>)
}