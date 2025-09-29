package net.thechance.chat.repository

import net.thechance.chat.entity.Contact
import net.thechance.chat.service.model.ContactModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

interface ContactRepository : JpaRepository<Contact, UUID> {

    @Query(
        """
            SELECT new net.thechance.chat.service.model.ContactModel(
                c.id,
                c.firstName,
                c.lastName,
                c.phoneNumber,
                CASE WHEN u IS NOT NULL THEN true ELSE false END,
                CASE WHEN u IS NOT NULL THEN u.imageUrl ELSE null END
            )
            FROM Contact c
            LEFT JOIN ContactUser u ON u.phoneNumber = c.phoneNumber
            WHERE c.contactOwnerId = :contactOwnerId
    """
    )
    fun findAllContactModelsByContactOwnerId(
        @Param("contactOwnerId") contactOwnerId: UUID,
        pageable: Pageable
    ): Page<ContactModel>


    @Modifying
    @Transactional
    @Query(
        value = """
    INSERT INTO 
    chat.contacts (
                    contact_owner_id,
                    phone_number,
                    first_name,
                    last_name
                    )   
        (
        SELECT :userId, t.phone_number, t.first_name, t.last_name
        FROM unnest(CAST(:phones AS text[]), CAST(:firstNames AS text[]), CAST(:lastNames AS text[])) 
             WITH ORDINALITY AS t(phone_number, first_name, last_name, ord)
        )
    ON CONFLICT (contact_owner_id, phone_number)
    DO UPDATE SET
        first_name = EXCLUDED.first_name,
        last_name  = EXCLUDED.last_name
    """,
        nativeQuery = true
    )
    fun bulkUpsert(
        @Param("userId") userId: UUID,
        @Param("phones") phones: Array<String>,
        @Param("firstNames") firstNames: Array<String>,
        @Param("lastNames") lastNames: Array<String>
    )

}