package net.thechance.chat.service

import jakarta.persistence.EntityManager
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import net.thechance.chat.entity.MessageAttachment
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageAttachmentRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val messageAttachmentRepository: MessageAttachmentRepository,
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
        val attachments = saveMessageAttachments(message)
        messageRepository.save(
            Message(
                id = message.id,
                senderId = message.senderId,
                chat = chat,
                messageAttachment = attachments,
                text = message.text,
                sentAt = message.sendAt,
            )
        )
    }

    private fun saveMessageAttachments(message: CreateMessageArgs): List<MessageAttachment> {
        val messageAttachments = mutableListOf<MessageAttachment>()
        message.attachments?.forEach { attachment ->
            val imageUrl = attachmentStorageService.uploadImage(
                file = attachment,
                fileName = "${message.id}-$attachment",
                folderName = FOLDER_NAME
            )
            val attachment = MessageAttachment(
                id = UUID.randomUUID(),
                message = entityManager.getReference(Message::class.java, message.id),
                url = imageUrl
            )
            messageAttachmentRepository.save(attachment)
            messageAttachments.add(attachment)
        }
        return messageAttachments
    }


    fun getAllChatMessages(chatId: UUID, pageable: Pageable) =
        messageRepository.getAllByChatIdOrderBySentAt(chatId, pageable)


    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) =
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)

    companion object {
        private const val FOLDER_NAME = "chat_attachments"
    }
}