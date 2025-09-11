package net.thechance.chat.service

import chat.dto.PagedResponse
import chat.mapper.toContactResponse
import net.thechance.chat.dto.ContactResponse
import net.thechance.chat.entity.Contact
import net.thechance.chat.repository.ContactRepository
import net.thechance.identity.repository.UserRepository
import net.thechance.mena.mapper.toPagedResponse
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class ContactService(
    private val contactRepository: ContactRepository,
    private val userRepository : UserRepository
) {

    fun getPagedContacts(userId: UUID, pageNumber: Int, pageSize: Int): PagedResponse<ContactResponse>{
        val pageable = PageRequest.of(pageNumber, pageSize)
        val pagedData =  contactRepository.findAllByOwnerUserId(
            userId,
            pageable
        )
        return pagedData.toPagedResponse {
            manageContact(it)
        }
    }

    private fun manageContact(contact : Contact): ContactResponse{
        val user = userRepository.findByPhoneNumber(contact.phoneNumber)
        return contact.toContactResponse(
            imageUrl = null,
            isMenaUser = user != null
        )
    }

}