package net.thechance.trends.api.dto.base

data class PagingResponse<T>(
    val pageNumber: Int,
    val results: List<T>,
    val totalResults: Int
) {
    companion object {
        fun <T> create(
            pageNumber: Int,
            results: List<T>,
            totalResults: Int
        ): PagingResponse<T> {
            return PagingResponse(
                pageNumber = maxOf(1, pageNumber),
                results = results,
                totalResults = totalResults
            )
        }
    }
}