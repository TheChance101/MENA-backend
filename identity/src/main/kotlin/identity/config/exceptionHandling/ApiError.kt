package identity.config.exceptionHandling

data class ApiError(
    val status: Int,
    val exception: String,
    val error: String,
    val message: String?,
    val errors: List<String>? = null
)
