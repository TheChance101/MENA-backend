package net.thechance.dukan.api.dto.shelf

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
data class DukanShelfTitleRequest(
    @field:NotBlank
    @field:Size(max = 50, message = "name must not exceed 50 characters")
    @field:Pattern(
        regexp = "^[A-Za-z-]+$",
        message = "name can only contain letters and '-'"
    )
    val title: String,
)