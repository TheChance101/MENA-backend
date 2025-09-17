package net.thechance.trends.api.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseErrorResponse(
    val message: String,
    val errors: List<String>?
)