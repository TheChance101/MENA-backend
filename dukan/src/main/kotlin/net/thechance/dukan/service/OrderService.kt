package net.thechance.dukan.service

import net.thechance.dukan.api.dto.order.OrderResponse
import net.thechance.dukan.api.mapper.order.toResponse
import net.thechance.dukan.repository.OrderRepository
import net.thechance.dukan.service.exception.ForbiddenException
import net.thechance.dukan.service.exception.OrderNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val dukanService: DukanService
) {
    fun getOrderByIdAndDukanId(userId: UUID, orderId: UUID): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { OrderNotFoundException() }

        val isDukanOwner = dukanService.getDukanDetailsById(order.dukanId).ownerId == userId
        val isCustomer = userId == order.userId

        if (isCustomer.not() && isDukanOwner.not()) throw ForbiddenException()

        return order.toResponse(isDukanOwner)
    }
}
