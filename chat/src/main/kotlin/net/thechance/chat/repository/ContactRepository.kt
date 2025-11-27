package net.thechance.chat.repository

import net.thechance.chat.entity.Contact
import net.thechance.chat.service.model.ContactModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ContactRepository : JpaRepository<Contact, UUID> {

    @Query(
        """
            SELECT new net.thechance.chat.service.model.ContactModel(
                c.id,
                c.firstName,
                c.lastName,
                c.phoneNumber,
                CASE WHEN u IS NOT NULL THEN u.id ELSE null END,
                CASE WHEN u IS NOT NULL THEN u.imageUrl ELSE null END
            )
            FROM Contact c
            LEFT JOIN ContactUser u 
                ON u.phoneNumber = c.phoneNumber 
                AND u.isDeleted = false
            WHERE c.contactOwnerId = :contactOwnerId
        """
    )
    fun findAllContactModelsByContactOwnerId(
        @Param("contactOwnerId") contactOwnerId: UUID,
        pageable: Pageable
    ): Page<ContactModel>


    fun findByContactOwnerIdAndPhoneNumber(ownerId: UUID, phoneNumber: String): Contact?

    @Modifying
    @Query("DELETE FROM Contact c WHERE c.contactOwnerId = :contactOwnerId AND c.phoneNumber IN (:phoneNumbers)")
    fun deleteByContactOwnerIdAndPhoneNumbers(contactOwnerId: UUID, phoneNumbers: List<String>)

    @Query(
        """
        SELECT new net.thechance.chat.service.model.ContactModel(
            c.id,
            c.firstName,
            c.lastName,
            c.phoneNumber,
            CASE WHEN u IS NOT NULL THEN u.id ELSE null END,
            CASE WHEN u IS NOT NULL THEN u.imageUrl ELSE null END
        )
        FROM Contact c
        LEFT JOIN ContactUser u 
            ON u.phoneNumber = c.phoneNumber
            AND u.isDeleted = false
        WHERE c.contactOwnerId = :contactOwnerId
          AND (
            LOWER(c.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            LOWER(c.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR
            c.phoneNumber LIKE CONCAT('%', :query, '%')
          )
          AND (:onlyMenaUsers = false OR u IS NOT NULL)
        """
    )
    fun searchContacts(
        @Param("contactOwnerId") contactOwnerId: UUID,
        @Param("query") query: String,
        @Param("onlyMenaUsers") onlyMenaUsers: Boolean,
        pageable: Pageable
    ): Page<ContactModel>
}