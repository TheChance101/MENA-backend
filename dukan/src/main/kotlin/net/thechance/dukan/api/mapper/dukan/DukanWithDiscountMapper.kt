package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.dukan.DukanWithDiscountResponse
import net.thechance.dukan.service.model.DukanWithDiscount

fun DukanWithDiscount.toResponse(): DukanWithDiscountResponse {
    return DukanWithDiscountResponse(
        id = dukan.id,
        imageUrl = dukan.imageUrl.orEmpty(),
        discount = discount
    )
}