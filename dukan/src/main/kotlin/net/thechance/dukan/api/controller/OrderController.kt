package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.order.OrderResponse
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.OrderService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("$DUKAN_PATH/order")
class OrderController(
    val orderService: OrderService
) {
    @GetMapping("/{orderId}")
    fun getOrderByIdAndUserId(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable orderId: UUID
    ): OrderResponse {
        return orderService.getOrderByIdAndUserId(
            userId = userId,
            orderId = orderId
        )
    }
}