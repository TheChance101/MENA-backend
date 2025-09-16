package chat.service


import chat.api.dto.ContactRequest
import chat.mapper.toContact
import chat.repository.ContactRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContactService(
    private val contactRepository: ContactRepository,
) {
    fun syncContacts(ownerUserId: UUID, contactRequests: List<ContactRequest>) {
        val requestedPhoneNumbers = contactRequests.map(ContactRequest::phoneNumber)

        val existingContacts = contactRepository
            .findAllByOwnerUserAndPhoneNumberIn(ownerUserId, requestedPhoneNumbers)
            .associateBy { it.phoneNumber }

        val contactsToSave = contactRequests.map { request ->
            existingContacts[request.phoneNumber]?.copy(
                firstName = request.firstName,
                lastName = request.lastName
            ) ?: request.toContact(
                ownerUserId
            )
        }

        val contactsToDelete = contactRepository.findAllByOwnerUserAndPhoneNumberNotIn(
            ownerUserId,
            requestedPhoneNumbers
        )

        contactRepository.deleteAll(contactsToDelete)
        contactRepository.saveAll(contactsToSave)

    }
}