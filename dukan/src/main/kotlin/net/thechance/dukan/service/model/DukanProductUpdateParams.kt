package net.thechance.dukan.service.model

import net.thechance.dukan.entity.Price
import java.util.UUID

data class DukanProductUpdateParams (
    val productId: UUID,
    val shelfId: UUID,
    val name: String,
    val description: String,
    val price: Price,
    val discountedPrice: Double? = null,
    val ownerId: UUID,
    val imageUrls: List<String>,
)