package net.thechance.dukan.api.dto

import net.thechance.dukan.entity.DukanProduct

fun DukanProduct.toProductResponse():DukanProductResponse{
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