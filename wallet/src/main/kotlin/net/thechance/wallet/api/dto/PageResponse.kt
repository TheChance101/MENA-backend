package net.thechance.wallet.api.dto

import org.springframework.data.domain.Page

data class PageResponse<T>(
    val totalElements: Long,
    val totalPages: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val content: List<T>
)


fun <T> Page<T>.toPageResponse(): PageResponse<T> =
    PageResponse(
        content = content,
        totalElements = totalElements,
        totalPages = totalPages,
        pageNumber = number,
        pageSize = size
    )
