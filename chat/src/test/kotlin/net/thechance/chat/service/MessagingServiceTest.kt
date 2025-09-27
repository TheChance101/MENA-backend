package net.thechance.chat.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.entity.Chat
import net.thechance.chat.repository.MessageRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class MessagingServiceTest {

    private lateinit var messageRepository: MessageRepository
    private lateinit var service: MessagingService

    private fun testUser(id: UUID = UUID.randomUUID()) = ContactUser(
        id = id,
        firstName = "Test",
        lastName = "User",
        phoneNumber = "123456789"
    )

    // Minimal Chat stub for Message entity
    private fun testChat(id: UUID = UUID.randomUUID()) = Chat(
        id = id,
        users = mutableSetOf(),
        messages = mutableSetOf()
    )

    @BeforeEach
    fun setUp() {
        messageRepository = mockk(relaxed = true)
        service = MessagingService(messageRepository)
    }

    @Test
    fun `markChatMessagesAsRead marks all unread messages as read in multiple pages`() {
        val chatId = UUID.randomUUID()
        val user = testUser()
        val chat = testChat(chatId)
        val pageSize = MessagingService.PAGE_SIZE

        val messagesPage1 = List(pageSize) {
            Message(UUID.randomUUID(), UUID.randomUUID(), "msg1", readByUsers = mutableSetOf(), chat = chat)
        }
        val messagesPage2 = List(pageSize) {
            Message(UUID.randomUUID(), UUID.randomUUID(), "msg2", readByUsers = mutableSetOf(), chat = chat)
        }
        val messagesPage3 = emptyList<Message>()

        every {
            messageRepository.findAllByChatIdAndReadByUsersNotContaining(eq(chatId), eq(user), any())
        } returnsMany listOf(messagesPage1, messagesPage2, messagesPage3)

        every { messageRepository.saveAll(any<List<Message>>()) } returnsArgument 0

        service.markChatMessagesAsRead(chatId, user)

        verify(exactly = 2) { messageRepository.saveAll(match<List<Message>> { it.all { msg -> user in msg.readByUsers } }) }
        verify(exactly = 3) { messageRepository.findAllByChatIdAndReadByUsersNotContaining(any(), any(), any()) }
    }

    @Test
    fun `markChatMessagesAsRead does nothing if no unread messages`() {
        val chatId = UUID.randomUUID()
        val user = testUser()
        every { messageRepository.findAllByChatIdAndReadByUsersNotContaining(chatId, user, any()) } returns emptyList()

        service.markChatMessagesAsRead(chatId, user)

        verify(exactly = 1) { messageRepository.findAllByChatIdAndReadByUsersNotContaining(chatId, user, any()) }
        verify(exactly = 0) { messageRepository.saveAll(any<List<Message>>()) }
    }

    @Test
    fun `PAGE_SIZE companion object is correct`() {
        assert(MessagingService.PAGE_SIZE == 500)
    }
}
