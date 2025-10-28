package net.thechance.dukan.service.exception

import net.thechance.dukan.api.utils.ErrorCodes.CART_NOT_FOUND
import net.thechance.dukan.api.utils.ErrorCodes.PRODUCT_NOT_IN_CART
import org.springframework.http.HttpStatus

class CartNotFoundException() : DukanException(
    code = CART_NOT_FOUND,
    status = HttpStatus.NOT_FOUND,
    message = "Cart not found"
)

class ProductNotInCartException() : DukanException(
    code = PRODUCT_NOT_IN_CART,
    status = HttpStatus.NOT_FOUND,
    message = "Product not found in cart"
)