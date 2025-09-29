package net.thechance.chat.api.dto

import net.thechance.chat.service.model.ContactModel
import net.thechance.chat.service.model.toResponse
import org.springframework.data.domain.Page


data class PagedResponse<T>(
    val data: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int
)

fun Page<ContactModel>.toResponse(): PagedResponse<ContactResponse>{
    return PagedResponse(
        data = this.content.map { it.toResponse() },
        pageNumber = this.number +1,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}