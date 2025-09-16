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
    fun syncContacts(userId: UUID, contactRequests: List<ContactRequest>) {
        val requestedPhoneNumbers = contactRequests.map { it.phoneNumber }

        val existingContactsMap = contactRepository
            .findAllByUserIdAndPhoneNumberIn(userId, requestedPhoneNumbers)
            .associateBy { it.phoneNumber }

        val contactsToSave = contactRequests.map { request ->
            existingContactsMap[request.phoneNumber]?.copy(
                firstName = request.firstName,
                lastName = request.lastName
            ) ?: request.toContact(userId)
        }

        contactRepository.deleteAllByUserIdAndPhoneNumberNotIn(userId, requestedPhoneNumbers)
        contactRepository.saveAll(contactsToSave)
    }
}