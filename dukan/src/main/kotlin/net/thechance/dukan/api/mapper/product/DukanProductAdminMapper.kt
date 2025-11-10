package net.thechance.dukan.api.mapper.product

import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.entity.DukanProduct

fun DukanProduct.toAdminResponse() = DukanProductResponse(
    id = this.id,
    name = this.name,
    shelfId = this.shelf.id,
    price = this.price,
    discountedPrice = null, //TODO
    description = this.description,
    imageUrls = this.imageUrls,
    quantityInCart = 0,
    createdAt = this.createdAt,
    isFavorite = false,
    dukanId = this.dukan.id
)