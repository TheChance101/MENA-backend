package net.thechance.dukan.api.mapper.product

import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.entity.DukanProduct

fun DukanProduct.toProductResponse(isFavorite: Boolean, quantityInCart: Int): DukanProductResponse {
    return DukanProductResponse(
        id = this.id,
        name = this.name,
        shelfId = this.shelf.id,
        price = this.price,
        description = this.description,
        imageUrls = this.imageUrls,
        quantityInCart = quantityInCart,
        isFavorite = isFavorite,
        createdAt = this.createdAt
    )
}