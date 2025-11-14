package net.thechance.chat.eventListener

import net.thechance.chat.eventListener.mapper.toUser
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.events.identity.UserCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ChatEventListener(
    private val userRepository: ContactUserRepository
) {


    @EventListener
    @Async
    fun onUserCreatedEvent(event: UserCreatedEvent) {
        userRepository.save(event.toUser())
    }
}