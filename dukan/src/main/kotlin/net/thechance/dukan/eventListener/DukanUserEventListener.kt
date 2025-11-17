package net.thechance.dukan.eventListener

import net.thechance.dukan.eventListener.mapper.toUser
import net.thechance.dukan.repository.DukanUserRepository
import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DukanUserEventListener(
    private val userRepository: DukanUserRepository
) {
    @EventListener
    @Async
    fun onUserCreatedEvent(event: UserCreatedEvent) {
        userRepository.save(event.toUser())
    }

    @EventListener
    @Async
    fun onUserUpdatedEvent(event: UserUpdatedEvent) {
        userRepository.save(event.toUser())
    }
}