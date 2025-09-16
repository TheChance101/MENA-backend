package chat.repository

import chat.entity.Contact
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContactRepository : JpaRepository<Contact, UUID> {
    fun findAllByOwnerUserAndPhoneNumberIn(
        ownerUser: UUID,
        phoneNumbers: List<String>
    ): List<Contact>

    fun findAllByOwnerUserAndPhoneNumberNotIn(
        ownerUser: UUID,
        phoneNumbers: Collection<String>
    ): List<Contact>
}
