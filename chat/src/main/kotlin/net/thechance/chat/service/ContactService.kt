package net.thechance.chat.service

import jakarta.transaction.Transactional
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
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Contact::firstName.name).ascending())
        )
    }

    @Transactional
    fun syncContacts(contactRequests: List<Contact>) {
        val uniqueContacts = contactRequests.distinctBy { it.phoneNumber }.toMutableList()

        if (uniqueContacts.isEmpty()) return
        val ownerId = contactRequests.first().contactOwnerId
        contactRepository.deleteByContactOwnerIdAndPhoneNumbers(ownerId, uniqueContacts.map { it.phoneNumber })
        contactRepository.saveAll(uniqueContacts)
    }

    fun getContactByOwnerIdAndContactUserId(ownerId: UUID, contactUserId: UUID): Contact? {
        val userPhone = contactUserService.getPhoneNumberByUserId(contactUserId)
        return contactRepository.findByContactOwnerIdAndPhoneNumber(ownerId, userPhone)
    }

    fun searchContacts(userId: UUID, query: String, onlyMenaUsers: Boolean, pageable: Pageable): Page<ContactModel> {
        return contactRepository.searchContacts(
            contactOwnerId = userId,
            query = query.trim(),
            onlyMenaUsers = onlyMenaUsers,
            pageable = pageable
        )
    }

}