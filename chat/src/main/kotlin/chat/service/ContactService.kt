package net.thechance.chat.service

import net.thechance.chat.entity.Contact
import net.thechance.chat.repository.ContactRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ContactService(
    private val repository: ContactRepository
) {

    fun getAllContacts(pageNumber: Int, pageSize: Int): Page<Contact>{
        val pageable = PageRequest.of(pageNumber, pageSize)
        return repository.findAll(pageable)
    }
}