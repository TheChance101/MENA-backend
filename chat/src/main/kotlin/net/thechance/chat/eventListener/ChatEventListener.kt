package net.thechance.chat.eventListener

import net.thechance.chat.eventListener.mapper.toOrderMessage
import net.thechance.chat.eventListener.mapper.toUser
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.ChatService
import net.thechance.events.dukan.OrderCreationEvent
import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ChatEventListener(
    private val userRepository: ContactUserRepository,
    private val messageRepository: MessageRepository,
    private val chatService: ChatService,

    ) {


    @EventListener
    @Async
    fun onUserCreatedEvent(event: UserCreatedEvent) {
        userRepository.save(event.toUser())
    }

    @EventListener
    @Async
    fun onUserUpdatedEvent(event: UserUpdatedEvent){
        userRepository.save(event.toUser())
    }

    @EventListener
    @Async
    fun onOrderCreated(event: OrderCreationEvent) {
        val message = messageRepository.save(event.toOrderMessage())
        chatService.sendMessageToChatParticipants(message)
    }

}