package net.thechance.app.exception

data class ApiErrorResponse(
    val status: Int,
    val exception: String,
    val error: String,
    val message: String?,
    val errors: List<String>? = null
)
