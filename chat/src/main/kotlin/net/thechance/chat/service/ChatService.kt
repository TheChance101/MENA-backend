package net.thechance.chat.service

import net.thechance.chat.entity.*
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.DeletedChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import net.thechance.chat.service.model.ChatSummary
import net.thechance.chat.service.model.MessageImageRequestArgs
import net.thechance.chat.service.model.MessageRequestArgs
import net.thechance.chat.service.model.toModel
import net.thechance.chat.service.model.toSummary
import net.thechance.chat.service.model.MessageAudioRequestArgs
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChatService(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val deletedChatRepository: DeletedChatRepository,
    private val contactUserService: ContactUserService,
    private val attachmentStorageService: AttachmentStorageService,
    private val contactService: ContactService
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
    fun saveMessage(args: MessageRequestArgs): Message {
        return messageRepository.save(
            Message(
                senderId = args.senderId,
                chatId = args.chatId,
                text = args.text,
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
                senderId = args.senderId,
                chatId = args.chatId,
                imageUrl = imageUrl
            )
        )
    }

    @Transactional
    fun saveMessageAudio(args: MessageAudioRequestArgs): Message {
        val audioUrl = attachmentStorageService.uploadAudio(
            file = args.audio,
            fileName = args.audio.originalFilename ?: "${Instant.now()}-Untitled",
            folderName = FOLDER_NAME
        )

        return messageRepository.save(
            Message(
                senderId = args.senderId,
                chatId = args.chatId,
                audioUrl = audioUrl
            )
        )
    }

    fun getAllChatMessages(chatId: UUID, pageable: Pageable) =
        messageRepository.getAllByChatIdOrderBySentAtDesc(chatId, pageable)


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
            chat.toSummary(
                userId,
                otherUser,
                lastMessages.firstOrNull { it.chatId == chat.id },
                unreadCounts.firstOrNull { it.chatId == chat.id }?.unreadCount ?: 0
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
        return chat.toSummary(
            userId,
            otherUser,
            lastMessage,
            unreadCount
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
    fun deleteChatById(chatId: UUID){
        chatRepository.findByIdOrNull(chatId) ?: throw NotFoundException("no chat found with this id $chatId")
        deletedChatRepository.save(DeletedChat(chatId = chatId, CleanUpStatus.PENDING))
    }

    private fun getChatName(contact: Contact?, user: ContactUser?): String {
        return contact?.let { "${it.firstName} ${it.lastName}" }
            ?: user?.let { "${it.firstName} ${it.lastName}" }.orEmpty()
    }


}