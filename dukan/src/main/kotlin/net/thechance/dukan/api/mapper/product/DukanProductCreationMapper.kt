package net.thechance.dukan.api.mapper.product

import net.thechance.dukan.api.dto.product.DukanProductCreationRequest
import net.thechance.dukan.api.dto.product.DukanProductUpdateRequest
import net.thechance.dukan.api.dto.product.PriceRequest
import net.thechance.dukan.entity.Price
import net.thechance.dukan.service.model.DukanProductCreationParams
import net.thechance.dukan.service.model.DukanProductUpdateParams
import java.util.UUID

fun DukanProductCreationRequest.toProductCreationParams(ownerId: UUID) = DukanProductCreationParams(
    name = name,
    ownerId = ownerId,
    shelfId = shelfId,
    description = description,
    price = price.toEntity(),
)

fun DukanProductUpdateRequest.toProductUpdateParams(ownerId: UUID, productId : UUID): DukanProductUpdateParams {
    return DukanProductUpdateParams(
        name = name,
        description = description,
        price = price.toEntity(),
        shelfId = shelfId,
        ownerId = ownerId,
        imageUrls = imageUrls,
        productId = productId,
    )
}
fun PriceRequest.toEntity(): Price {
    return Price(
        base = base,
        final =final ?: this.base
    )
}