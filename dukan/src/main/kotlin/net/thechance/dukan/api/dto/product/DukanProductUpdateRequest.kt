package net.thechance.dukan.api.dto.product

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class DukanProductUpdateRequest(
    @field:NotBlank(message = "product name must not be blank")
    val name: String,
    @field:Size(min = 100, max = 3000, message = "description must be between 100 and 3000 characters")
    @field:NotBlank(message = "description must not be blank")
    val description: String,
    @field:NotNull(message = "price is required")
    val price: PriceRequest,
    @field:NotNull(message = "must choose at least one shelf")
    val shelfId: UUID,
    @field:Size(min = 1, max = 10, message = "must have at least one image and not execute then 10")
    val imageUrls: List<String>
)