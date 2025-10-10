package net.thechance.chat.service

import jakarta.persistence.EntityManager
import net.thechance.chat.api.dto.MessageImagesRequestDto
import net.thechance.chat.api.dto.toMessageArgs
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import net.thechance.chat.entity.MessageImages
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageImagesRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val messageImagesRepository: MessageImagesRepository,
    private val chatRepository: ChatRepository,
    private val contactUserService: ContactUserService,
    private val attachmentStorageService: AttachmentStorageService,
    private val entityManager: EntityManager
) {
    @Transactional
    fun getOrCreateConversationByParticipants(userId: UUID, receiverId: UUID): Chat {
        val users = setOf(userId, receiverId)

        val existingChat = chatRepository.findByUsersIds(users)
        if (existingChat != null) return existingChat

        val requester = contactUserService.getUserById(userId)
        val otherUser = contactUserService.getUserById(receiverId)

        return chatRepository.save(Chat(users = mutableSetOf(requester, otherUser)))
    }

    @Transactional
    fun saveMessage(message: CreateMessageArgs): Message {
        val chat = entityManager.getReference(Chat::class.java, message.chatId)
        val message = Message(
            id = message.id,
            senderId = message.senderId,
            chat = chat,
            text = message.text,
            sentAt = message.sendAt,
        )
        messageRepository.save(message)
        return message
    }

    @Transactional
    fun saveMessageImages(senderId: UUID, request: MessageImagesRequestDto): Message {
        val message = saveMessage(request.toMessageArgs(senderId))
        val messageImages = mutableListOf<MessageImages>()
        request.images.forEach { image ->
            val imageUrl = attachmentStorageService.uploadImage(
                file = image,
                fileName = "${message.id}-$image",
                folderName = FOLDER_NAME
            )
            val image = MessageImages(
                id = UUID.randomUUID(),
                url = imageUrl,
                message = message
            )
            messageImagesRepository.save(image)
            messageImages.add(image)
        }
        return message.copy(images = messageImages)
    }


    fun getAllChatMessages(chatId: UUID, pageable: Pageable) =
        messageRepository.getAllByChatIdOrderBySentAtDesc(chatId, pageable)


    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) =
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)

    companion object {
        private const val FOLDER_NAME = "chat_attachments"
    }
}