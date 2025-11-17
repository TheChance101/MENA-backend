package net.thechance.dukan.api.mapper.product

import net.thechance.dukan.api.dto.product.DukanProductAdminResponse
import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity
import java.math.BigDecimal


fun DukanProductWithFavoriteAndQuantity.toResponse() = DukanProductResponse(
    id = this.product.id,
    name = this.product.name,
    shelfId = this.product.shelf.id,
    price = this.product.price,
    discount = product.discount ?: BigDecimal.ZERO,
    description = this.product.description,
    imageUrls = this.product.imageUrls,
    quantityInCart = this.quantity,
    createdAt = this.product.createdAt,
    isFavorite = this.isFavorite,
    dukanId = this.product.dukan.id,
    isOutOfStock = this.product.isOutOfStock,
    )

fun DukanProduct.toAdminResponse() = DukanProductAdminResponse(
    id = this.id,
    name = this.name,
    finalPrice = this.price.final,
    basePrice = this.price.base,
    description = this.description,
    imageUrls = this.imageUrls,
    createdAt = this.createdAt,
)