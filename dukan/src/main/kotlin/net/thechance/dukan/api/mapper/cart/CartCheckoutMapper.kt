package net.thechance.dukan.api.mapper.cart

import net.thechance.dukan.api.dto.cart.CartCheckoutRequest
import net.thechance.dukan.api.dto.cart.CartCheckoutResponse
import net.thechance.dukan.service.model.CartCheckoutParams
import net.thechance.dukan.service.model.CartCheckoutPreview

fun CartCheckoutRequest.toParams(): CartCheckoutParams {
    return CartCheckoutParams(
        cartId = this.cartId,
        address = this.address,
        longitude = this.longitude,
        latitude = this.latitude
    )
}

fun CartCheckoutPreview.toResponse(): CartCheckoutResponse {
    return CartCheckoutResponse(
        transactionId = transactionId,
        totalAmount = totalAmount
    )
}