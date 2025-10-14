package net.thechance.chat.service

import jakarta.persistence.EntityManager
import net.thechance.chat.api.dto.MessageImagesRequestArgs
import net.thechance.chat.api.dto.MessageRequestArgs
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.entity.MessageImages
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageImagesRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val messageImagesRepository: MessageImagesRepository,
    private val chatRepository: ChatRepository,
    private val contactUserService: ContactUserService,
    private val attachmentStorageService: AttachmentStorageService,
    private val contactService: ContactService,
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
    fun saveMessage(args: MessageRequestArgs): Message {
        args.messageId?.let { messageId ->
            messageRepository.findById(messageId).orElse(null)?.let { return it }
        }
        val chat = entityManager.getReference(Chat::class.java, args.chatId)
        val message = Message(
            id = UUID.randomUUID(),
            senderId = args.senderId,
            chat = chat,
            text = args.text,
            sentAt = Instant.now(),
        )
        messageRepository.save(message)
        return message
    }

    @Transactional
    fun saveMessageImages(args: MessageImagesRequestArgs): Message {
        val message = saveMessage(MessageRequestArgs(args.chatId, args.senderId, null, null))
        val messageImages = mutableListOf<MessageImages>()
        args.images.forEach { image ->
            val imageUrl = attachmentStorageService.uploadImage(
                file = image,
                fileName = "${message.id}-$image",
                folderName = FOLDER_NAME
            )
            val image = MessageImages(
                id = UUID.randomUUID(),
                url = imageUrl,
                messageId = message.id
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

    fun getChatById(chatId: UUID, userId: UUID): ChatModel {
        val chat = chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        val otherUser = chat.users.firstOrNull { it.id != userId }
        val contact =otherUser?.let{
            contactService.getContactByOwnerIdAndContactUserId(userId, it.id)
        }
        return ChatModel(
            name = getChatName(contact, otherUser),
            imageUrl = otherUser?.imageUrl,
            requesterId = userId,
            id = chatId
        )
    }

    private fun getChatName(contact: Contact?, user: ContactUser? ): String{
        return contact?.let { "${it.firstName} ${it.lastName}" }
            ?: user?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
    }


    companion object {
        private const val FOLDER_NAME = "chat_attachments"
    }
}