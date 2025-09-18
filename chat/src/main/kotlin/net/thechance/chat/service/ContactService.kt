package net.thechance.chat.service

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
            contactRepository.findAllByUserId(userId, Pageable.unpaged(Sort.by("firstName").ascending()))
        } else {
            val sortedPageable =
                PageRequest.of(pageable.pageNumber - 1, pageable.pageSize, Sort.by("firstName").ascending())
            contactRepository.findAllByUserId(userId, sortedPageable)
        }
        return pagedData.map { it.toModel(isMenaUser = false, imageUrl = "https://picsum.photos/200") }
    }

}