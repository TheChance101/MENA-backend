package net.thechance.dukan.api.mapper.cart

import net.thechance.dukan.api.dto.cart.CartItemResponse
import net.thechance.dukan.api.dto.cart.CartResponse
import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.CartItem

fun CartItem.toResponse(): CartItemResponse {
    return CartItemResponse(
        productId = this.product.id,
        productName = this.product.name,
        description = this.product.description,
        quantity = this.quantity,
        price = this.product.price,
        imageUrl = this.product.imageUrls.firstOrNull()
    )
}

fun Cart.toResponse(): CartResponse {
    val discountPercentage = getDiscountPercentage()
    return CartResponse(
        id = this.id,
        totalPriceBeforeDiscount = price.base,
        totalPriceAfterDiscount = price.final,
        discount =discountPercentage
    )
}
