package net.thechance.dukan.api.dto.product

import java.time.Instant
import java.util.UUID

data class DukanProductResponse(
    val id: UUID,
    val name:String,
    val dukanId:UUID,
    val shelfId: UUID,
    val price:Double,
    val discountedPrice: Double?,
    val description:String,
    val imageUrls:List<String>,
    val quantityInCart: Int,
    val createdAt: Instant,
    val isFavorite: Boolean
)