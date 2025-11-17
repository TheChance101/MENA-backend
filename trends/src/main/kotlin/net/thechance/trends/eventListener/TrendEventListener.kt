package net.thechance.trends.eventListener

import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserStatusUpdatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import net.thechance.trends.entity.TrendUser
import net.thechance.trends.repository.TrendUserRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import net.thechance.events.identity.utils.Status as EventStatus
import net.thechance.trends.entity.TrendUser.Status as TrendUserStatus

@Component
class TrendEventListener(
    private val userRepository: TrendUserRepository
) {

    private fun EventStatus.toTrendUserStatus(): TrendUserStatus {
        return when (this) {
            EventStatus.ACTIVE -> TrendUserStatus.ACTIVE
            EventStatus.BLOCKED -> TrendUserStatus.BLOCKED
        }
    }

    @EventListener
    @Async
    fun handleUserCreated(event: UserCreatedEvent) {
        val trendUser = TrendUser(
            userId = event.id,
            phoneNumber = event.phoneNumber,
            firstName = event.firstName,
            lastName = event.lastName,
            username = event.username,
            imageUrl = event.imageUrl,
            status = event.status.toTrendUserStatus()
        )
        userRepository.save(trendUser)
    }

    @EventListener
    @Async
    fun handleUserUpdated(event: UserUpdatedEvent) {
        val trendUser = TrendUser(
            userId = event.id,
            phoneNumber = event.phoneNumber,
            firstName = event.firstName,
            lastName = event.lastName,
            username = event.username,
            imageUrl = event.imageUrl,
            status = event.status.toTrendUserStatus()
        )
        userRepository.save(trendUser)
    }

    @EventListener
    @Async
    fun handleUserStatusUpdated(event: UserStatusUpdatedEvent) {
        val status = when (event.status) {
            UserStatusUpdatedEvent.UserStatus.BLOCKED -> TrendUserStatus.BLOCKED
            UserStatusUpdatedEvent.UserStatus.ACTIVE -> TrendUserStatus.ACTIVE
        }
        userRepository.updateUserStatus(event.userId, status)
    }
}