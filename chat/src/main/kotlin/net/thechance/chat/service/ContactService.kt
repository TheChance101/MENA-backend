package net.thechance.chat.service

import jakarta.transaction.Transactional
import net.thechance.chat.entity.Contact
import net.thechance.chat.repository.ContactRepository
import net.thechance.chat.service.exception.InvalidPhoneNumberException
import net.thechance.chat.service.model.ContactModel
import net.thechance.chat.service.model.SearchContactsArgs
import net.thechance.chat.service.phone_number.PhoneNumberParses
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
    private val phoneNumberParses: PhoneNumberParses
) {

    fun getPagedContactByUserId(userId: UUID, pageable: Pageable): Page<ContactModel> {
        return contactRepository.findAllContactModelsByContactOwnerId(
            userId,
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Contact::firstName.name).ascending())
        )
    }

    @Transactional
    fun syncContacts(contactRequests: List<Contact>, ownerId: UUID) {
        val checkedUniqueContacts = parseContacts(contactRequests, ownerId)
            .distinctBy { it.phoneNumber }
            .takeIf { it.isNotEmpty() } ?: return

        contactRepository.deleteByContactOwnerIdAndPhoneNumbers(ownerId, checkedUniqueContacts.map { it.phoneNumber })
        contactRepository.saveAll(checkedUniqueContacts)
    }

    fun parseContacts(contacts: List<Contact>, ownerId: UUID): List<Contact> {
        if (contacts.isEmpty()) return emptyList()
        val ownerPhoneNumber = contactUserService.getPhoneNumberByUserId(ownerId) ?: return emptyList()
        val ownerRegion = phoneNumberParses.parse(ownerPhoneNumber, "").regionCode
        return contacts
            .mapNotNull { contact ->
                try {
                    val validatedPhoneNumber = phoneNumberParses.parse(contact.phoneNumber, ownerRegion)
                    contact.copy(phoneNumber = validatedPhoneNumber.phoneNumber).takeIf { it.phoneNumber != ownerPhoneNumber }
                } catch (_: InvalidPhoneNumberException) {
                    null
                }
            }
    }

    fun getContactByOwnerIdAndContactUserId(ownerId: UUID, contactUserId: UUID): Contact? {
        val userPhone = contactUserService.getPhoneNumberByUserId(contactUserId) ?: return null
        return contactRepository.findByContactOwnerIdAndPhoneNumber(ownerId, userPhone)
    }

    fun searchContacts(args: SearchContactsArgs): Page<ContactModel> {
        return contactRepository.searchContacts(
            contactOwnerId = args.userId,
            query = args.query.trim(),
            onlyMenaUsers = args.onlyMenaUsers,
            pageable = args.pageable
        )
    }

}