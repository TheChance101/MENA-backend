package net.thechance.faith.api.dto.nearestMosque

import jakarta.validation.constraints.*
import org.springframework.web.multipart.MultipartFile

data class MosqueRequest(
    @field:NotBlank(message = "Mosque name must not be blank")
    val name: String,
    @field:NotBlank(message = "Address must not be blank")
    val address: String,
    @field:NotNull(message = "Latitude is required")
    @field:DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @field:DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    val latitude: Double,
    @field:NotNull(message = "Longitude is required")
    @field:DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @field:DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    val longitude: Double,
    @field:NotBlank(message = "Image file must not be blank")
    val image: MultipartFile,
)
