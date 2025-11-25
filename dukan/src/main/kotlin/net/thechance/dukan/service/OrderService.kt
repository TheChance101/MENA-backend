package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Cart
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanUser
import net.thechance.dukan.entity.Order
import net.thechance.dukan.entity.OrderItem
import net.thechance.dukan.repository.OrderRepository
import net.thechance.dukan.service.exception.ForbiddenException
import net.thechance.dukan.service.exception.OrderNotFoundException
import net.thechance.dukan.service.model.OrderWithDukanOwner
import net.thechance.events.dukan.OrderCreationEvent
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val dukanService: DukanService
) {
    fun getOrderByIdAndUserId(userId: UUID, orderId: UUID): OrderWithDukanOwner {
        val order = orderRepository.findById(orderId).orElseThrow { OrderNotFoundException() }

        val isDukanOwner = dukanService.getDukanDetailsById(order.dukanId).ownerId == userId
        val isCustomer = userId == order.userId

        if (isCustomer.not() && isDukanOwner.not()) throw ForbiddenException()

        return OrderWithDukanOwner(order = order, isDukanOwner = isDukanOwner)
    }

    @Transactional
    fun createOrderFromCart(
        orderId: UUID,
        cart: Cart,
        user: DukanUser,
        dukan: Dukan,
        transactionId: UUID
    ): Order {

        val order = Order(
            id = orderId,
            userId = cart.userId,
            dukanId = cart.dukanId,
            transactionId = transactionId,
            totalBeforeDiscount = cart.price.base,
            discountPercentage = cart.getDiscountPercentage(),
            totalAfterDiscount = cart.price.final,
            platformFees = cart.platformFees,
            customerName = user.userName,
            customerPhone = user.phoneNumber,
            customerImage = user.imageUrl ?: "",
            deliveryAddress = user.address,
            deliveryLat = user.latitude,
            deliveryLng = user.longitude,
            dukanLongitude = dukan.longitude,
            dukanLatitude = dukan.latitude
        )

        cart.items.forEach { item ->
            order.items.add(
                OrderItem(
                    order = order,
                    productId = item.product.id,
                    productName = item.product.name,
                    productImage = item.product.imageUrls.first(),
                    priceBeforeDiscount = item.product.price.base,
                    quantity = item.quantity,
                    priceAfterDiscount = item.product.price.final
                )
            )
        }

        return orderRepository.save(order)
    }

    fun createOrderCreationEvent(order: Order, ownerId: UUID): OrderCreationEvent {
        return OrderCreationEvent(
            orderId = order.id,
            userId = order.userId,
            dukanId = order.dukanId,
            dukanOwnerId = ownerId,
            totalProducts = order.items.size,
            totalPrice = order.totalAfterDiscount,
            deliverToAddress = order.deliveryAddress
        )
    }
}