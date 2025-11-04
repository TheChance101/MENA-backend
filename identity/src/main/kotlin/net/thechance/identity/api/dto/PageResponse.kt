package net.thechance.identity.api.dto

data class PageResponse<T>(
    val totalElements: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val items: List<T>
)