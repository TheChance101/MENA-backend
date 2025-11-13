package net.thechance.dukan.api.dto.product

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class DukanProductCreationRequest(
    @field:NotBlank(message = "product name must not be blank")
    val name: String,
    @field:Size(min = 100, max = 3000, message = "description must be between 100 and 3000 characters")
    @field:NotBlank(message = "description must not be blank")
    val description: String,
    @field:NotNull(message = "price is required")
    val price: PriceRequest,
    @field:NotNull(message = "must choose at least one shelf")
    val shelfId: UUID
)