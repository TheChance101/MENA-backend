package net.thechance.chat.service


import net.thechance.chat.api.dto.ContactRequest
import net.thechance.chat.repository.ContactRepository
import net.thechance.chat.api.dto.toContact
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContactService(
    private val contactRepository: ContactRepository,
) {
    fun syncContacts(ownerUserId: UUID, contactRequests: List<ContactRequest>) {
        val requestedPhoneNumbers = contactRequests.map(ContactRequest::phoneNumber)

        val existingContacts = contactRepository
            .findAllByUserIdAndPhoneNumberIn(ownerUserId, requestedPhoneNumbers)
            .associateBy { it.phoneNumber }

        val contactsToSave = contactRequests.map { request ->
            existingContacts[request.phoneNumber]?.copy(
                firstName = request.firstName,
                lastName = request.lastName
            ) ?: request.toContact(
                ownerUserId
            )
        }

        val contactsToDelete = contactRepository.findAllByUserIdAndPhoneNumberNotIn(
            ownerUserId,
            requestedPhoneNumbers
        )

        contactRepository.deleteAll(contactsToDelete)
        contactRepository.saveAll(contactsToSave)

    }
}