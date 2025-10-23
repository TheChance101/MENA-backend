package net.thechance.dukan.service.mapper.product

import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.entity.DukanProduct

fun DukanProduct.toProductResponse(): DukanProductResponse {
    return DukanProductResponse(
        id = this.id,
        name = this.name,
        shelfId = this.shelf.id,
        price = this.price,
        description = this.description,
        imageUrls = this.imageUrls,
        createdAt = this.createdAt
    )
}