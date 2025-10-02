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
    private val contactRepository: ContactRepository,
    private val contactUserService: ContactUserService,
) {

    fun getPagedContactByUserId(userId: UUID, pageable: Pageable): Page<ContactModel> {
        return contactRepository.findAllContactModelsByContactOwnerId(
            userId,
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by("firstName").ascending())
        )
    }

    fun syncContacts(userId: UUID, contactRequests: List<Contact>) {
        val uniqueContacts = contactRequests.distinctBy { it.phoneNumber }

        if (uniqueContacts.isEmpty()) return

        val phones = uniqueContacts.map { it.phoneNumber }.toTypedArray()
        val firstNames = uniqueContacts.map { it.firstName }.toTypedArray()
        val lastNames = uniqueContacts.map { it.lastName }.toTypedArray()

        contactRepository.bulkUpsert(userId, phones, firstNames, lastNames)
    }

    fun getContactByOwnerIdAndContactUserId(ownerId: UUID, contactUserId: UUID): Contact? {
        val userPhone = contactUserService.getPhoneNumberByUserId(contactUserId)
        return contactRepository.findByContactOwnerIdAndPhoneNumber(ownerId, userPhone)
    }
}