package net.thechance.chat.api.controller

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import net.thechance.chat.api.controller.ChatController.Companion.QUEUE_MESSAGES
import net.thechance.chat.api.dto.ChatResponse
import net.thechance.chat.api.dto.MarkAsReadRequest
import net.thechance.chat.api.dto.MarkAsReadResponse
import net.thechance.chat.api.dto.MessageRequestDto
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.service.ChatService
import net.thechance.chat.service.ContactService
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.security.Principal
import java.time.Instant
import java.util.*

class ChatControllerTest {

    private val messagingTemplate: SimpMessagingTemplate = mockk(relaxed = true)
    private val chatService: ChatService = mockk()
    private val contactService: ContactService = mockk()

    private val controller by lazy {
        ChatController(
            messagingTemplate,
            chatService,
            contactService,
        )
    }

    @Test
    fun `sendPrivateMessage should save message and send to user`() {
        val chatId = UUID.randomUUID()
        val senderId = UUID.randomUUID()
        val dto = MessageRequestDto(chatId, "message1")

        val principal = mockk<Principal>()
        every { principal.name } returns senderId.toString()

        justRun { chatService.saveMessage(any()) }
        justRun { messagingTemplate.convertAndSendToUser(any(), any(), any()) }

        controller.sendPrivateMessage(dto, principal)

        verify { chatService.saveMessage(match { it.text == "message1" && it.chatId == chatId && it.senderId == senderId }) }

    }


    @Test
    fun `getOrCreateConversation should return conversation`() {
        val userId = UUID.randomUUID()
        val receiverId = UUID.randomUUID()
        val chatId = UUID.randomUUID()

        every { contactService.getContactByOwnerIdAndContactUserId(userId, receiverId) } returns Contact(
            id = UUID.randomUUID(),
            firstName = "Ali",
            lastName = "Ahmed",
            phoneNumber = "777777777",
            contactOwnerId = userId
        )

        val chat = Chat(
            id = chatId,
            users = mutableSetOf(
                ContactUser(id = userId, firstName = "User1", lastName = "Test", phoneNumber = "123456789"),
                ContactUser(id = receiverId, firstName = "User2", lastName = "Test", phoneNumber = "777777777")
            )
        )
        every { chatService.getOrCreateConversationByParticipants(userId, receiverId) } returns chat

        val response = controller.getOrCreateConversation(userId, receiverId)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.id).isEqualTo(chatId)
    }


    @Test
    fun `getChatHistory should return paged messages`() {
        val chatId = UUID.randomUUID()
        val chat = Chat(id = chatId, users = mutableSetOf(), messages = mutableSetOf())
        val pageable: Pageable = PageRequest.of(0, 10)

        val messages = listOf(
            Message(
                id = UUID.randomUUID(),
                chat = chat,
                senderId = UUID.randomUUID(),
                text = "Hi",
                sentAt = Instant.now(),
                isRead = false
            )
        )

        val page = PageImpl(messages, pageable, messages.size.toLong())

        every { chatService.getAllChatMessages(chatId, pageable) } returns page

        val response = controller.getChatHistory(chatId, pageable)

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
        every { chatService.markChatMessagesAsRead(chatId, userId) } returns 1

        controller.markMessagesAsRead(markAsReadRequest, principal)

        verify {
            messagingTemplate.convertAndSendToUser(
                chatId.toString(),
                QUEUE_MESSAGES,
                MarkAsReadResponse(userId)
            )
        }
        verify { chatService.markChatMessagesAsRead(chatId, userId) }
    }

    @Test
    fun `getChatDetail should get chat response correctly when the function run successfully `(){
        every {chatService.getChatById(chatId, userId)} returns chatModel
        val result = controller.getChatDetail(chatId, userId).body

        assertThat(result).isEqualTo(chatResponse)
    }

    @Test
    fun `getChatDetail should throw not found exception when try to find unavailable chatId`(){
        every {chatService.getChatById(chatId, userId)} throws NotFoundException("")
        assertThrows<NotFoundException> {
            controller.getChatDetail(chatId = chatId, userId = userId)
        }
    }

    private companion object{
        val chatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-16ba3869610e")
        val userId = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c404c6")

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
    }
}
