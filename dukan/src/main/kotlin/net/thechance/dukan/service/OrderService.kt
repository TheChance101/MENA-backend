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
    fun getOrderByIdAndDukanId(userId: UUID, dukanId: UUID, orderId: UUID): OrderResponse {
        val order = orderRepository.findByIdAndDukanId(orderId, dukanId)
            ?: throw OrderNotFoundException()

        if (userId != order.userId) throw ForbiddenException()

        val isDukanOwner = dukanService.getDukanDetailsById(dukanId).ownerId == userId
        return order.toResponse(isDukanOwner)
    }
}
