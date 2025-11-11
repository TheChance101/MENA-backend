package net.thechance.dukan.api.mapper.product

import net.thechance.dukan.api.dto.product.DukanProductAdminResponse
import net.thechance.dukan.entity.DukanProduct

fun DukanProduct.toAdminResponse() = DukanProductAdminResponse(
    id = this.id,
    name = this.name,
    price = this.price,
    discountedPrice = null, //TODO
    description = this.description,
    imageUrls = this.imageUrls,
    createdAt = this.createdAt,
)