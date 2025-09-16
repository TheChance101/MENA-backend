package net.thechance.faith.api.controller.exception

data class BaseErrorResponse(
    val message: String,
    val errors: List<String>? = null
)