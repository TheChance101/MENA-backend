package net.thechance.chat.repository

import net.thechance.chat.entity.Contact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ContactRepository : JpaRepository<Contact, UUID> {
    fun findAllByContactOwnerIdAndPhoneNumberIn(contactOwnerId: UUID, phoneNumbers: List<String>): List<Contact>
    fun deleteAllByContactOwnerIdAndPhoneNumberNotIn(contactOwnerId: UUID, requestedPhoneNumbers: List<String>)
}
