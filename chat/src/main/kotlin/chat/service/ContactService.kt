package net.thechance.chat.service

import chat.service.ContactModel
import chat.service.toModel
import net.thechance.chat.repository.ContactRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ContactService(
    private val contactRepository: ContactRepository
) {
    fun getPagedContact(pageable: Pageable): Page<ContactModel> {
        val pagedData = contactRepository.findAll(pageable)
        return pagedData.map { it.toModel(isMenaUser = false, imageUrl = "") }
    }

}