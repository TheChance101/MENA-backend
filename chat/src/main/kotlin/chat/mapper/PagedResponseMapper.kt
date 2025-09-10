package net.thechance.mena.mapper

import chat.dto.PagedResponse
import org.springframework.data.domain.Page

fun <T> Page<T>.toPagedResponse(): PagedResponse<T> {
    return PagedResponse(
        data = content,
        pageNumber = number,
        pageSize = size,
        totalItems = totalElements,
        totalPages = totalPages
    )
}