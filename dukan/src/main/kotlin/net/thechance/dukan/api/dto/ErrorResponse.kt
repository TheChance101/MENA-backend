package net.thechance.dukan.api.dto

data class ErrorResponse(
    val message: String,
    val errors: Map<String, String>? = null,
    val errorCode: Int? = null
)