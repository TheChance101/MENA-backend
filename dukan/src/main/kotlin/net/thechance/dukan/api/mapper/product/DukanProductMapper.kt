package net.thechance.dukan.api.mapper.product

import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity


fun DukanProductWithFavoriteAndQuantity.toResponse() = DukanProductResponse(
    id = this.product.id,
    name = this.product.name,
    shelfId = this.product.shelf.id,
    price = this.product.price,
    discount = product.discount,
    description = this.product.description,
    imageUrls = this.product.imageUrls,
    quantityInCart = this.quantity,
    createdAt = this.product.createdAt,
    isFavorite = this.isFavorite,
    dukanId = this.product.dukan.id
    )