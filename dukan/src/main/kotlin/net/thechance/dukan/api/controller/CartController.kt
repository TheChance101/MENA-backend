package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.cart.AddToCartRequest
import net.thechance.dukan.api.dto.cart.CartPageResponse
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.CartService
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
    fun addOrUpdateItem(
        @AuthenticationPrincipal userId: UUID, @RequestBody request: AddToCartRequest
    ): ResponseEntity<Unit> {
        cartService.addOrUpdateItem(userId, request.dukanId, request.productId, request.quantity)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{dukanId}")
    fun getCart(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable dukanId: UUID,
        @PageableDefault(size = 10, page = 0, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<CartPageResponse> {
        val cartResponse = cartService.fetchCartWithPagedItems(userId, dukanId, pageable)
        return ResponseEntity.ok(cartResponse)
    }

    @DeleteMapping("/{dukanId}/items/{productId}")
    fun removeItem(@AuthenticationPrincipal userId: UUID, @PathVariable dukanId: UUID, @PathVariable productId: UUID) {
        ResponseEntity.ok(cartService.removeItem(userId, dukanId, productId))
    }
}