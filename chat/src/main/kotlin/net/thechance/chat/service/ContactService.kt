package net.thechance.chat.service


import net.thechance.chat.repository.ContactRepository
import net.thechance.chat.entity.Contact
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContactService(
    private val contactRepository: ContactRepository,
) {
    fun syncContacts(userId: UUID, contactRequests: List<Contact>) {

        val uniqueContactRequests = contactRequests
            .groupBy { it.phoneNumber }
            .map { it.value.last() }
        val requestedPhoneNumbers = uniqueContactRequests.map { it.phoneNumber }

        val existingContactsMap = contactRepository
            .findAllByContactOwnerIdAndPhoneNumberIn(userId, requestedPhoneNumbers)
            .associateBy { it.phoneNumber }

        val contactsToSave = uniqueContactRequests.map { request ->
            existingContactsMap[request.phoneNumber]?.copy(
                firstName = request.firstName,
                lastName = request.lastName
            ) ?: request
        }

        contactRepository.deleteAllByContactOwnerIdAndPhoneNumberNotIn(userId, requestedPhoneNumbers)
        contactRepository.saveAll(contactsToSave)
    }
}