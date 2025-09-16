package chat.api.dto

import org.springframework.data.domain.Page


data class PagedResponse<T>(
    val data: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int
)

fun <T> Page<T>.toResponse(): PagedResponse<T>{
    return PagedResponse(
        data = this.content,
        pageNumber = this.number,
        pageSize = this.size,
        totalItems = this.totalElements,
        totalPages = this.totalPages
    )
}