package net.thechance.chat.service

import net.thechance.chat.entity.Contact
import net.thechance.chat.repository.ContactRepository
import net.thechance.chat.service.model.ContactModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContactService(
    private val contactRepository: ContactRepository
) {

    fun getPagedContactByUserId(userId: UUID, pageable: Pageable): Page<ContactModel> {
        return if (pageable.pageNumber <= 0 || pageable.pageSize <= 0) {
            contactRepository.findAllContactModelsByContactOwnerId(
                userId,
                Pageable.unpaged(Sort.by("firstName").ascending())
            )
        } else {
            contactRepository.findAllContactModelsByContactOwnerId(
                userId,
                PageRequest.of(pageable.pageNumber - 1, pageable.pageSize, Sort.by("firstName").ascending())
            )
        }
    }

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