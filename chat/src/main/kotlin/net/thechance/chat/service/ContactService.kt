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
        val uniqueContacts = contactRequests
            .groupBy { it.phoneNumber }
            .map { it.value.last() }

        if (uniqueContacts.isEmpty()) return

        uniqueContacts.chunked(CHUNK_SIZE).forEach { chunk ->
            val phones = chunk.map { it.phoneNumber }.toTypedArray()
            val firstNames = chunk.map { it.firstName }.toTypedArray()
            val lastNames = chunk.map { it.lastName }.toTypedArray()

            contactRepository.bulkUpsert(userId, phones, firstNames, lastNames)
        }
    }

    private companion object {
        const val CHUNK_SIZE = 1000
    }
}