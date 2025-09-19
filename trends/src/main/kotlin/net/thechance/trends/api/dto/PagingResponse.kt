package net.thechance.trends.api.dto

data class PagingResponse<T>(
    val pageNumber: Int,
    val results: List<T>,
    val totalResults: Int
)