package net.thechance.dukan.api.dto

data class ProductCreationRequest(
    val name: String,
    val description: String,
    val price: Double,
)