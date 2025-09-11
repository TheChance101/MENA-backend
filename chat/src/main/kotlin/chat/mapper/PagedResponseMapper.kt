package net.thechance.mena.mapper

import chat.dto.PagedResponse
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus

fun <T> Page<T>.toPagedResponse(): PagedResponse<T> {
    return PagedResponse(
        data = content,
        pageNumber = number,
        pageSize = size,
        totalItems = totalElements,
        totalPages = totalPages
    )
}

fun <T, S> Page<T>.toPagedResponse(
    convertor :(T)->S
): PagedResponse<S> {
    return PagedResponse(
        data = content.map { convertor(it) },
        pageNumber = number,
        pageSize = size,
        totalItems = totalElements,
        totalPages = totalPages
    )
}