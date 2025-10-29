package net.thechance.faith.api.dto.nearestMosque

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class MosqueRequest(
    @field:NotBlank(message = "product name must not be blank")
    val name: String,
    @field:NotBlank(message = "address name must not be blank")
    val address: String,
    @field:DecimalMin("-90.0", message = "latitude must be >= -90")
    @field:DecimalMax("90.0", message = "latitude must be <= 90")
    val latitude: Double,
    @field:DecimalMin("-180.0", message = "longitude must be >= -180")
    @field:DecimalMax("180.0", message = "longitude must be <= 180")
    val longitude: Double,
    val createdAt: LocalDate

)