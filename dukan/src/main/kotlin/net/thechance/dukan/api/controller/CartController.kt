package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.dto.cart.AddToCartRequest
import net.thechance.dukan.api.dto.cart.CartCheckoutRequest
import net.thechance.dukan.api.dto.cart.CartCheckoutResponse
import net.thechance.dukan.api.dto.cart.CartItemResponse
import net.thechance.dukan.api.dto.cart.CartResponse
import net.thechance.dukan.api.mapper.cart.toParams
import net.thechance.dukan.api.mapper.cart.toResponse
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.CartService
import net.thechance.dukan.service.model.AddOrUpdateCartItemParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("$DUKAN_PATH/cart")
class CartController(
    private val cartService: CartService
) {

    @PostMapping("/items")
    fun addItem(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody request: AddToCartRequest
    ): ResponseEntity<Unit> {
        cartService.addItem(
            AddOrUpdateCartItemParams(userId, request.dukanId, request.productId, request.quantity)
        )
        return ResponseEntity.ok().build()
    }

    @PutMapping("/items")
    fun updateItem(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody request: AddToCartRequest
    ): ResponseEntity<Unit> {
        cartService.updateItem(
            AddOrUpdateCartItemParams(userId, request.dukanId, request.productId, request.quantity)
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{dukanId}/info")
    fun getCartInfo(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable dukanId: UUID
    ): ResponseEntity<CartResponse> {
        val cart = cartService.getCartOrThrow(userId, dukanId)
        val cartResponse = cart.toResponse()
        return ResponseEntity.ok(cartResponse)
    }

    @GetMapping("/{dukanId}/items")
    fun getCartItems(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable dukanId: UUID,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<CartItemResponse>> {
        val items = cartService.getCartItems(userId, dukanId, pageable)
        val cartItemsResponse = items.map { it.toResponse() }
        return ResponseEntity.ok(cartItemsResponse)
    }

    @DeleteMapping("/{dukanId}/items/{productId}")
    fun removeItem(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable dukanId: UUID,
        @PathVariable productId: UUID
    ): ResponseEntity<Unit> {
        cartService.removeItem(userId, dukanId, productId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/checkout")
    fun checkout(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody checkoutRequest: CartCheckoutRequest
    ): ResponseEntity<CartCheckoutResponse> {
        val cartCheckout = cartService.checkout(userId, checkoutRequest.toParams())
        return ResponseEntity.ok(cartCheckout.toResponse())
    }
}