package net.thechance.app.exception

data class ApiErrorResponse(
    val status: Int,
    val message: String,
)
