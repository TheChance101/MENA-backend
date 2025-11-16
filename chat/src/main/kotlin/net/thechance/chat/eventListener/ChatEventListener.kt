package net.thechance.chat.eventListener

import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.entity.Message
import net.thechance.chat.eventListener.mapper.toUser
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.ChatService
import net.thechance.chat.service.model.MessageContent
import net.thechance.events.identity.UserCreatedEvent
import net.thechance.events.identity.UserUpdatedEvent
import net.thechance.events.wallet.TransactionCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ChatEventListener(
    private val userRepository: ContactUserRepository,
    private val chatService: ChatService,
    private val messageRepository: MessageRepository,
    private val messagingTemplate: SimpMessagingTemplate,
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
            TransactionCompletedEvent.TransactionType.ONLINE_PURCHASE -> return // TODO: handle online purchases with order message
            TransactionCompletedEvent.TransactionType.DEPOSIT -> return // no messages for deposit
            TransactionCompletedEvent.TransactionType.P2P -> handleP2PTransactionEvent(event)
        }
    }

    private fun handleP2PTransactionEvent(event: TransactionCompletedEvent){
        val chat = chatService.getChatByUserIds(event.senderId, event.receiverId)
        val message = Message(
            chatId = chat.id,
            senderId = event.senderId,
            type = Message.MessageType.MONEY,
            content = MessageContent.Money(amount = event.amount.toDouble()),
        )
        messageRepository.save(message)

        chatService.getChatUsersIds(chat.id).forEach { chatParticipantId ->
            messagingTemplate.convertAndSendToUser(
                chatParticipantId.toString(),
                PRIVATE_MESSAGES,
                message.toResponse(chatParticipantId)
            )
        }
    }

    companion object {
        const val PRIVATE_MESSAGES = "/private/messages"
    }

}