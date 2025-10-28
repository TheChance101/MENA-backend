package net.thechance.dukan.api.dto.cart

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CartPageResponse(
    @field:JsonProperty("id")
    val id: UUID,
    @field:JsonProperty("dukan_id")
    val dukanId: UUID,
    @field:JsonProperty("total_price")
    val totalPrice: Double,
    @field:JsonProperty("items")
    val items: List<CartItemResponse>,
    @field:JsonProperty("pagination")
    val pagination: PageInfoResponse
) {
    data class PageInfoResponse(
        @field:JsonProperty("page")
        val page: Int,
        @field:JsonProperty("size")
        val size: Int,
        @field:JsonProperty("total_pages")
        val totalPages: Int,
        @field:JsonProperty("total_items")
        val totalItems: Long
    )
}

