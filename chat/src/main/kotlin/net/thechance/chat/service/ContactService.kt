package net.thechance.chat.service

import net.thechance.chat.entity.Contact
import net.thechance.chat.repository.ContactRepository
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
        val pagedData = if (pageable.pageNumber <= 0 || pageable.pageSize <= 0) {
            contactRepository.findAllByContactOwnerId(userId, Pageable.unpaged(Sort.by("firstName").ascending()))
        } else {
            val sortedPageable =
                PageRequest.of(pageable.pageNumber - 1, pageable.pageSize, Sort.by("firstName").ascending())
            contactRepository.findAllByContactOwnerId(userId, sortedPageable)
        }
        return pagedData.map { it.toModel(isMenaUser = false, imageUrl = "https://picsum.photos/200") }
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