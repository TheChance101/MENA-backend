package net.thechance.chat.eventListener

import net.thechance.chat.api.controller.MessageSender
import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.eventListener.mapper.toMessage
import net.thechance.chat.eventListener.mapper.toUser
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.ChatService
import net.thechance.events.identity.UserCreatedEvent
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
    fun listenToMoneyTransactionsEvent(event: TransactionCompletedEvent) {
        if (event.senderId == event.receiverId) return
        when (event.type) {
            TransactionCompletedEvent.TransactionType.ONLINE_PURCHASE -> return
            TransactionCompletedEvent.TransactionType.DEPOSIT -> return
            TransactionCompletedEvent.TransactionType.P2P -> handleP2PTransactionEvent(event)
        }
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