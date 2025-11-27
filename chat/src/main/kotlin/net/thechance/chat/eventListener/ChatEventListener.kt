package net.thechance.chat.eventListener

import net.thechance.chat.api.controller.MessageSender
import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.eventListener.mapper.toMessage
import net.thechance.chat.eventListener.mapper.toOrderMessage
import net.thechance.chat.eventListener.mapper.toUser
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.ChatService
import net.thechance.chat.service.ContactUserService
import net.thechance.events.dukan.OrderCreationEvent
import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserDeletedEvent
import net.thechance.events.identity.UserUpdatedEvent
import net.thechance.events.wallet.TransactionCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ChatEventListener(
    private val userRepository: ContactUserRepository,
    private val chatService: ChatService,
    private val messageRepository: MessageRepository,
    private val messageSender: MessageSender,
    private val contactUserService: ContactUserService
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
        val chat = chatService.getChatByUserIds(event.dukanOwnerId, event.userId)
        val message = messageRepository.save(event.toOrderMessage(chat.id))
        messageSender.sendMessageToChat(chat.id, PRIVATE_MESSAGES, { message.toResponse(it) })
    }

    @EventListener
    @Async
    fun listenToMoneyTransactionsEvent(event: TransactionCompletedEvent) {
        if (event.senderId == event.receiverId) return
        when (event.type) {
            TransactionCompletedEvent.TransactionType.ONLINE_PURCHASE -> return
            TransactionCompletedEvent.TransactionType.DEPOSIT -> return
            TransactionCompletedEvent.TransactionType.P2P -> handleP2PTransactionEvent(event)
        }
    }

    @EventListener
    @Async
    fun onUserDeleted(event: UserDeletedEvent) {
        contactUserService.deleteUser(userId = event.id)
    }

    private fun handleP2PTransactionEvent(event: TransactionCompletedEvent){
        val chat = chatService.getChatByUserIds(event.senderId, event.receiverId)
        val message = event.toMessage(chat.id)
        messageRepository.save(message)
        messageSender.sendMessageToChat(chat.id, PRIVATE_MESSAGES, { message.toResponse(it) })
    }

    companion object {
        const val PRIVATE_MESSAGES = "/private/messages"
    }
}