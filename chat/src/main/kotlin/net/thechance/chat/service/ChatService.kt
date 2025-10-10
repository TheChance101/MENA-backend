package net.thechance.chat.service

import jakarta.persistence.EntityManager
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
    fun saveMessage(message: CreateMessageArgs) {
        val chat = entityManager.getReference(Chat::class.java, message.chatId)
        val images = saveMessageImages(message)
        messageRepository.save(
            Message(
                id = message.id,
                senderId = message.senderId,
                chat = chat,
                images = images,
                text = message.text,
                sentAt = message.sendAt,
            )
        )
    }

    @Transactional
    private fun saveMessageImages(message: CreateMessageArgs): List<MessageImages> {
        val messageImages = mutableListOf<MessageImages>()
        message.images?.forEach { attachment ->
            val imageUrl = attachmentStorageService.uploadImage(
                file = attachment,
                fileName = "${message.id}-$attachment",
                folderName = FOLDER_NAME
            )
            val images = MessageImages(
                id = UUID.randomUUID(),
                url = imageUrl
            )
            messageImagesRepository.save(images)
            messageImages.add(images)
        }
        return messageImages
    }


    fun getAllChatMessages(chatId: UUID, pageable: Pageable) =
        messageRepository.getAllByChatIdOrderBySentAtDesc(chatId, pageable)


    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) =
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)

    companion object {
        private const val FOLDER_NAME = "chat_attachments"
    }
}