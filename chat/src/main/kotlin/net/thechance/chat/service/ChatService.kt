package net.thechance.chat.service

import net.thechance.chat.api.controller.ChatController
import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.entity.*
import net.thechance.chat.eventListener.mapper.toOrderMessage
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.DeletedChatRepository
import net.thechance.chat.repository.MessageReactionRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.exception.InvalidTimeFormatException
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.*
import net.thechance.events.dukan.OrderCreationEvent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val messageReactionRepository: MessageReactionRepository,
    private val chatRepository: ChatRepository,
    private val deletedChatRepository: DeletedChatRepository,
    private val contactUserService: ContactUserService,
    private val attachmentStorageService: AttachmentStorageService,
    private val contactService: ContactService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    @Transactional
    fun getChatByUserIds(userId: UUID, receiverId: UUID): ChatModel {
        val usersId = setOf(userId, receiverId)
        val requester = contactUserService.getUserById(userId)
        val otherUser = contactUserService.getUserById(receiverId)
        val contact = contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id)

        val chatName = getChatName(contact, otherUser)
        val imageUrl = otherUser.imageUrl.orEmpty()

        val chat = findOrCreateChat(
            usersId = usersId,
            requester = requester,
            otherUser = otherUser
        ).toModel(
            chatName = chatName,
            imageUrl = imageUrl,
            requesterId = userId
        )
        return chat
    }

    private fun findOrCreateChat(usersId: Set<UUID>, requester: ContactUser, otherUser: ContactUser): Chat {
        return chatRepository.findByUsersIds(usersId)
            ?: chatRepository.save(Chat(users = mutableSetOf(requester, otherUser)))
    }

    @Transactional
    fun saveTextMessage(args: MessageRequestArgs): Message {
        return messageRepository.save(
            Message(
                id = args.messageId,
                senderId = args.senderId,
                chatId = args.chatId,
                type = Message.MessageType.TEXT,
                content = MessageContent.Text(args.text)
            )
        )
    }
    
    fun saveAyahMessage(args: MessageAyahRequestArgs): Message{
        return messageRepository.save(
            Message(
                id = args.messageId,
                senderId = args.senderId,
                type = Message.MessageType.AYAH,
                content = MessageContent.Ayah(
                    ayahNumber = args.ayahNumber,
                    suraNumber = args.suraNumber,
                    ayahText = args.ayahText
                ),
                chatId = args.chatId
            )
        )
    }

    @Transactional
    fun saveMessageImage(args: MessageImageRequestArgs): Message {
        val imageUrl = attachmentStorageService.uploadImage(
            file = args.image,
            folderName = args.chatId.toString()
        )

        return messageRepository.save(
            Message(
                id = args.messageId,
                senderId = args.senderId,
                chatId = args.chatId,
                type = Message.MessageType.IMAGE,
                content = MessageContent.Image(imageUrl)
            )
        )
    }

    @Transactional
    fun saveMessageAudio(args: MessageAudioRequestArgs): Message {
        val audioUrl = attachmentStorageService.uploadAudio(
            file = args.audio,
            fileName = args.audio.originalFilename ?: "${Instant.now()}-Untitled",
            folderName = FOLDER_NAME,
        )

        return messageRepository.save(
            Message(
                id = args.messageId,
                senderId = args.senderId,
                chatId = args.chatId,
                type = Message.MessageType.AUDIO,
                content = MessageContent.Audio(audioUrl, args.audioDurationMs)
            )
        )
    }

    fun getMessageById(messageId: UUID): Message {
        return messageRepository.findByIdOrNull(messageId)
            ?: throw NotFoundException("no message was found with id: $messageId")
    }

    @Transactional
    fun addReaction(args: MessageReactionRequestArgs): MessageReaction {
        val existing = messageReactionRepository.findByMessageIdAndUserId(args.messageId, args.userId)

        messageRepository.updateUpdatedAt(args.messageId)

        return existing?.copy(emoji = args.emoji)?.let { messageReactionRepository.save(it) }
            ?: messageReactionRepository.save(
                MessageReaction(
                    messageId = args.messageId,
                    userId = args.userId,
                    emoji = args.emoji
                )
            )
    }

    fun deleteReaction(args: MessageReactionRequestArgs): MessageReaction {
        messageRepository.updateUpdatedAt(args.messageId)

        val reaction = messageReactionRepository.findByMessageIdAndUserId(args.messageId, args.userId)
            ?: throw NotFoundException("no message reactions was found")

        messageReactionRepository.deleteByMessageIdAndUserId(args.messageId, args.userId)
        return reaction
    }

    fun getAllChatMessagesByChatId(chatId: UUID, pageable: Pageable): Page<Message> {
        return messageRepository.getAllByChatIdOrderBySentAtDesc(chatId, pageable)
    }

    fun getLatestMessagesAfter(chatId: UUID, updatedAfter: Instant?, pageable: Pageable): Page<Message> {
        if (updatedAfter == null) throw InvalidTimeFormatException("Invalid Time Format : $updatedAfter")
        return messageRepository.findAllByChatIdAndLastModifiedAtAfterOrderByLastModifiedAtAsc(
            chatId,
            updatedAfter,
            pageable
        )
    }

    fun markChatMessagesAsRead(chatId: UUID, userId: UUID) {
        messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId = chatId, userId = userId)
    }

    fun getUserChatsSummaries(userId: UUID, pageable: Pageable): Page<ChatSummary> {
        val chats = chatRepository.findAllByUserId(userId, pageable)
        val chatIds = chats.content.map { it.id }

        val lastMessages = messageRepository.findLastMessagesForChats(chatIds)
        val unreadCounts = chatRepository.findUnreadCountsForChats(chatIds)

        val chatsSummaries = chats.map { chat ->
            val otherUser = chat.users.firstOrNull { it.id != userId }
            val contact = otherUser?.let { contactService.getContactByOwnerIdAndContactUserId(userId, it.id) }
            val chatName = getChatName(contact, otherUser)
            val imageUrl = otherUser?.imageUrl.orEmpty()

            chat.toSummary(
                userId = userId,
                chatName = chatName,
                imageUrl = imageUrl,
                lastMessage = lastMessages.firstOrNull { it.chatId == chat.id },
                unreadCount = unreadCounts.firstOrNull { it.chatId == chat.id }?.unreadCount ?: 0
            )
        }
        return chatsSummaries
    }

    fun getUserChatSummaryById(chatId: UUID, userId: UUID): ChatSummary {
        val chat =
            chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        val otherUser = chat.users.firstOrNull { it.id != userId }
        val lastMessage = messageRepository.findTopByChatIdOrderBySentAtDesc(chatId)
        val unreadCount = chatRepository.findUnreadCountsForChats(listOf(chatId)).firstOrNull()?.unreadCount ?: 0
        val contact = otherUser?.let { contactService.getContactByOwnerIdAndContactUserId(userId, it.id) }
        val chatName = getChatName(contact, otherUser)
        val imageUrl = otherUser?.imageUrl.orEmpty()
        return chat.toSummary(
            userId = userId,
            chatName = chatName,
            imageUrl = imageUrl,
            lastMessage = lastMessage,
            unreadCount = unreadCount
        )
    }

    fun getChatById(chatId: UUID, userId: UUID): ChatModel {
        val chat =
            chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        val otherUser = chat.users.first { it.id != userId }
        val contact = otherUser.let {
            contactService.getContactByOwnerIdAndContactUserId(userId, it.id)
        }
        return ChatModel(
            name = getChatName(contact, otherUser),
            imageUrl = otherUser.imageUrl,
            requesterId = userId,
            id = chatId
        )
    }

    fun getChatUsersIds(chatId: UUID): List<UUID> {
        val chat =
            chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat was found with id: $chatId")
        return chat.users.map { it.id }
    }

    @Transactional
    fun deleteChatById(chatId: UUID) {
        val currentChat =
            chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat found with this id $chatId")
        val deletedChats = currentChat.users.map {
            DeletedChat(
                chatId = chatId,
                cleanUpStatus = CleanUpStatus.PENDING,
                userId = it.id,
            )
        }
        deletedChatRepository.saveAll(deletedChats)
    }
    fun handleOrderCreated(event: OrderCreationEvent) {
        val message = messageRepository.save(event.toOrderMessage())
        sendMessageToChatParticipants(message)
    }

    fun sendMessageToChatParticipants(message: Message) {
        val chatParticipants = getChatUsersIds(message.chatId)
        chatParticipants.forEach { userId ->
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                ChatController.PRIVATE_MESSAGES,
                message.toResponse(userId)
            )
        }
    }

    fun getDeletedChatsIdByUserIdAfterSpecificTime(userId: UUID, time: Instant): List<String> {
        return deletedChatRepository.getDeletedChatsIdByUserIdAfterSpecificTime(
            userId = userId,
            time = time
        ).map { it.toString() }
    }

    private fun getChatName(contact: Contact?, user: ContactUser?): String {
        return contact?.let { "${it.firstName} ${it.lastName}" }
            ?: user?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
    }


    companion object {
        private const val FOLDER_NAME = "chat_attachments"
    }
}