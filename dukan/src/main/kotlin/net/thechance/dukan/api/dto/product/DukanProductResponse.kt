package net.thechance.dukan.api.dto.product

import net.thechance.dukan.entity.Price
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DukanProductResponse(
    val id: UUID,
    val name:String,
    val dukanId:UUID,
    val shelfId: UUID,
    val price: Price,
    val discount: BigDecimal,
    val description:String,
    val imageUrls:List<String>,
    val quantityInCart: Int,
    val createdAt: Instant,
    val isFavorite: Boolean,
    val isOutOfStock: Boolean,
)

data class DukanProductAdminResponse(
    val id: UUID,
    val name:String,
    val price: BigDecimal,
    val discountedPrice: BigDecimal,
    val description:String,
    val imageUrls:List<String>,
    val createdAt: Instant,
)