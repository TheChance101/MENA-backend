package net.thechance.chat.api.controller

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import net.thechance.chat.api.controller.ChatController.Companion.MARK_AS_READ
import net.thechance.chat.api.dto.*
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Message
import net.thechance.chat.service.ChatService
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import net.thechance.chat.service.model.MessageImageRequestArgs
import net.thechance.chat.service.model.MessageWithReactions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.multipart.MultipartFile
import java.security.Principal
import java.time.Instant
import java.util.*

class ChatControllerTest {

    private val messagingTemplate: SimpMessagingTemplate = mockk(relaxed = true)
    private val chatService: ChatService = mockk()

    private val controller by lazy {
        ChatController(
            messagingTemplate,
            chatService,
        )
    }

    @Test
    fun `sendPrivateMessage should save message and send to user`() {
        val chatId = UUID.randomUUID()
        val senderId = UUID.randomUUID()
        val dto = MessageRequestDto(chatId, "message1")

        val principal = mockk<Principal>()
        every { principal.name } returns senderId.toString()

        val savedMessage = testMessage(
            chat = Chat(id = chatId, users = mutableSetOf()),
            senderId = senderId,
            text = "message1",
        )

        every { chatService.saveMessage(any()) } returns savedMessage
        every { chatService.getChatUsersIds(chatId) } returns listOf(userId)
        justRun { messagingTemplate.convertAndSendToUser(any(), any(), any()) }

        controller.sendPrivateMessage(dto, principal)

        verify {
            chatService.saveMessage(
                match { it.senderId == senderId && it.text == "message1" && it.chatId == chatId },
            )
        }
    }

    @Test
    fun `sendMessageImages should upload image then send to user`() {
        val chatId = UUID.randomUUID()
        val senderId = UUID.randomUUID()

        val principal = mockk<Principal>()
        every { principal.name } returns senderId.toString()

        val image = mockk<MultipartFile>()

        val dummyChat = mockk<Chat>()
        every { dummyChat.id } returns chatId

        val savedMessage = testMessage(senderId = senderId, chat = dummyChat)

        every { chatService.saveMessageImage(any()) } returns savedMessage
        every { chatService.saveMessage(any()) } returns savedMessage
        every { chatService.getChatUsersIds(chatId) } returns listOf(userId)
        justRun { messagingTemplate.convertAndSendToUser(any(), any(), any()) }

        val messageImageArgs = MessageImageRequest(chatId, image)
        controller.sendMessageImage(messageImageArgs, principal)

        verify {
            chatService.saveMessageImage(match { it == MessageImageRequestArgs(chatId, senderId, image) })
        }
    }

    @Test
    fun `getChatByUserIds should return conversation`() {
        val requester = userId
        val receiverId = userId2
        every { chatService.getChatByUserIds(requester, receiverId) } returns chatModel

        val response = controller.getChatByUserIds(requester, receiverId)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.id).isEqualTo(chatId)
    }


    @Test
    fun `getChatHistory should return paged messages`() {
        val chatId = UUID.randomUUID()
        val pageable: Pageable = PageRequest.of(0, 10)

        val messagesWithReactions = listOf(
            MessageWithReactions(
                message = Message(
                    id = UUID.randomUUID(),
                    chatId = chatId,
                    senderId = UUID.randomUUID(),
                    text = "Hi",
                    sentAt = Instant.now(),
                    isRead = false
                ),
                reactions = emptyList()
            )

        )

        val page = PageImpl(messagesWithReactions, pageable, messagesWithReactions.size.toLong())

        every { chatService.getAllChatMessagesWithReactions(chatId, pageable) } returns page

        val response = controller.getChatHistory(chatId, UUID.randomUUID(), pageable)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.data).hasSize(1)
    }


    @Test
    fun `markMessagesAsRead should send read event and update service`() {
        val chatId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val principal = mockk<Principal>()
        val markAsReadRequest = MarkAsReadRequest(chatId)

        every { principal.name } returns userId.toString()
        justRun { messagingTemplate.convertAndSendToUser(any(), any(), any()) }
        every { chatService.markChatMessagesAsRead(chatId, userId) } returns Unit
        every { chatService.getChatUsersIds(chatId) } returns listOf(userId)
        every { chatService.markChatMessagesAsRead(chatId, userId) } just runs

        controller.markMessagesAsRead(markAsReadRequest, principal)

        verify {
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                MARK_AS_READ,
                MarkAsReadResponse(userId, chatId, true)
            )
        }
        verify { chatService.markChatMessagesAsRead(chatId, userId) }
    }

    @Test
    fun `getChatDetail should get chat response correctly when the function run successfully `() {
        every { chatService.getChatById(chatId, userId) } returns chatModel
        val result = controller.getChatDetail(chatId, userId).body

        assertThat(result).isEqualTo(chatResponse)
    }

    @Test
    fun `getChatDetail should throw not found exception when try to find unavailable chatId`() {
        every { chatService.getChatById(chatId, userId) } throws NotFoundException("")
        assertThrows<NotFoundException> {
            controller.getChatDetail(chatId = chatId, userId = userId)
        }
    }

    private companion object {
        val chatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-16ba3869610e")
        val userId = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c404c6")
        val userId2 = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c40986")

        val chatModel = ChatModel(
            name = "Raouf kamel",
            imageUrl = null,
            requesterId = userId,
            id = chatId
        )

        val chatResponse = ChatResponse(
            name = "Raouf kamel",
            imageUrl = null,
            requesterId = userId,
            id = chatId
        )

        private fun testMessage(senderId: UUID, chat: Chat, text: String? = null) = Message(
            id = UUID.randomUUID(),
            senderId = senderId,
            chatId = chatId,
            text = text,
            sentAt = Instant.now()
        )
    }
}
