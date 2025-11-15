package net.thechance.chat.service

import jakarta.transaction.Transactional
import net.thechance.chat.entity.Contact
import net.thechance.chat.repository.ContactRepository
import net.thechance.chat.service.exception.InvalidPhoneNumberException
import net.thechance.chat.service.model.ContactModel
import net.thechance.chat.service.phone_number.MenaCountry
import net.thechance.chat.service.phone_number.PhoneNumberValidator
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
    private val phoneNumberValidator: PhoneNumberValidator
) {

    fun getPagedContactByUserId(userId: UUID, pageable: Pageable): Page<ContactModel> {
        return contactRepository.findAllContactModelsByContactOwnerId(
            userId,
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Contact::firstName.name).ascending())
        )
    }

    @Transactional
    fun syncContacts(contactRequests: List<Contact>) {
        val checkedUniqueContacts = contactRequests
            .let(::checkValidContacts)
            .distinctBy { it.phoneNumber }
            .takeIf { it.isNotEmpty() } ?: return

        val ownerId = checkedUniqueContacts.first().contactOwnerId
        contactRepository.deleteByContactOwnerIdAndPhoneNumbers(ownerId, checkedUniqueContacts.map { it.phoneNumber })
        contactRepository.saveAll(checkedUniqueContacts)
    }

    fun checkValidContacts(contacts: List<Contact>): List<Contact> {
        if (contacts.isEmpty()) return emptyList()
        val ownerId = contacts.first().contactOwnerId
        val ownerPhoneNumber = contactUserService.getUserById(ownerId).phoneNumber
        val ownerRegion = MenaCountry.fromPhoneNumber(ownerPhoneNumber)?.countryCodeName
            ?: throw InvalidPhoneNumberException("can't get the region of the owner phone number")
        return contacts
            .mapNotNull { contact ->
                try {
                    val validatedPhoneNumber = phoneNumberValidator.validateAndParse(contact.phoneNumber, ownerRegion)
                    contact.copy(phoneNumber = validatedPhoneNumber.phoneNumber).takeIf { it.phoneNumber != ownerPhoneNumber }
                } catch (e: InvalidPhoneNumberException) {
                    null
                }
            }
    }


    fun getContactByOwnerIdAndContactUserId(ownerId: UUID, contactUserId: UUID): Contact? {
        val userPhone = contactUserService.getPhoneNumberByUserId(contactUserId)
        return contactRepository.findByContactOwnerIdAndPhoneNumber(ownerId, userPhone)
    }
}