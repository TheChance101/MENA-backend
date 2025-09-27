package net.thechance.chat.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.entity.Chat
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import net.thechance.chat.service.model.toModel
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals

class ChatServiceTest {

    private lateinit var messageRepository: MessageRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var contactUserRepository: ContactUserRepository
    private lateinit var service: ChatService

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
        chatRepository = mockk(relaxed = true)
        contactUserRepository = mockk(relaxed = true)
        service = ChatService(messageRepository, chatRepository, contactUserRepository)
    }

    @Test
    fun `markChatMessagesAsRead marks all unread messages as read in multiple pages`() {
        val chatId = UUID.randomUUID()
        val user = testUser()
        val chat = testChat(chatId)
        val pageSize = ChatService.PAGE_SIZE

        val messagesPage1 = List(pageSize) {
            Message(UUID.randomUUID(), UUID.randomUUID(), "msg1", readByUsers = mutableSetOf(), chat = chat)
        }
        val messagesPage2 = List(pageSize) {
            Message(UUID.randomUUID(), UUID.randomUUID(), "msg2", readByUsers = mutableSetOf(), chat = chat)
        }
        val messagesPage3 = emptyList<Message>()

        every {
            messageRepository.findAllByChatIdAndReadByUsersNotContainingAndSenderIdNot(eq(chatId), eq(user), eq(user.id), any())
        } returnsMany listOf(messagesPage1, messagesPage2, messagesPage3)

        every { messageRepository.saveAll(any<List<Message>>()) } returnsArgument 0

        service.markChatMessagesAsRead(chatId, user)

        verify(exactly = 2) { messageRepository.saveAll(match<List<Message>> { it.all { msg -> user in msg.readByUsers } }) }
        verify(exactly = 3) { messageRepository.findAllByChatIdAndReadByUsersNotContainingAndSenderIdNot(any(), any(), any(), any()) }
    }

    @Test
    fun `markChatMessagesAsRead does nothing if no unread messages`() {
        val chatId = UUID.randomUUID()
        val user = testUser()
        every { messageRepository.findAllByChatIdAndReadByUsersNotContainingAndSenderIdNot(chatId, user, user.id, any()) } returns emptyList()

        service.markChatMessagesAsRead(chatId, user)

        verify(exactly = 1) { messageRepository.findAllByChatIdAndReadByUsersNotContainingAndSenderIdNot(chatId, user, user.id, any()) }
        verify(exactly = 0) { messageRepository.saveAll(any<List<Message>>()) }
    }

    @Test
    fun `PAGE_SIZE companion object is correct`() {
        assert(ChatService.PAGE_SIZE == 500)
    }

    @Test
    fun `getOrCreateConversationByParticipants returns existing chat if found`() {
        val requester = testUser()
        val theOtherUser = testUser()
        val chat = testChat().apply { users.addAll(listOf(requester, theOtherUser)) }
        val chatModel = chat.toModel(requester.id)

        every { contactUserRepository.findById(requester.id) } returns Optional.of(requester)
        every { contactUserRepository.findById(theOtherUser.id) } returns Optional.of(theOtherUser)
        every { chatRepository.findByUsersIdsAndGroupChatIsNull(setOf(requester.id, theOtherUser.id)) } returns chat

        val result = service.getOrCreateConversationByParticipants(requester.id, theOtherUser.id)

        assertEquals(chatModel, result)
        verify { chatRepository.findByUsersIdsAndGroupChatIsNull(setOf(requester.id, theOtherUser.id)) }
        verify(exactly = 0) { chatRepository.save(any()) }
    }

    @Test
    fun `getOrCreateConversationByParticipants creates and returns new chat if not found`() {
        val requester = testUser()
        val theOtherUser = testUser()
        val newChat = testChat().apply { users.addAll(listOf(requester, theOtherUser)) }
        val chatModel = newChat.toModel(requester.id)

        every { contactUserRepository.findById(requester.id) } returns Optional.of(requester)
        every { contactUserRepository.findById(theOtherUser.id) } returns Optional.of(theOtherUser)
        every { chatRepository.findByUsersIdsAndGroupChatIsNull(setOf(requester.id, theOtherUser.id)) } returns null
        every { chatRepository.save(any()) } returns newChat
        every { chatRepository.findByIdIs(newChat.id) } returns newChat

        val result = service.getOrCreateConversationByParticipants(requester.id, theOtherUser.id)

        assertEquals(chatModel, result)
        verify { chatRepository.findByIdIs(newChat.id) }
    }

    @Test
    fun `getOrCreateConversationByParticipants throws if requester not found`() {
        val requesterId = UUID.randomUUID()
        val theOtherUser = testUser()
        every { contactUserRepository.findById(requesterId) } returns Optional.empty()

        try {
            service.getOrCreateConversationByParticipants(requesterId, theOtherUser.id)
            assert(false) // Should not reach here
        } catch (e: IllegalArgumentException) {
            assert(e.message!!.contains("Requester with id"))
        }
    }

    @Test
    fun `getOrCreateConversationByParticipants throws if the other user not found`() {
        val requester = testUser()
        val theOtherUserId = UUID.randomUUID()
        every { contactUserRepository.findById(requester.id) } returns Optional.of(requester)
        every { contactUserRepository.findById(theOtherUserId) } returns Optional.empty()

        try {
            service.getOrCreateConversationByParticipants(requester.id, theOtherUserId)
            assert(false) // Should not reach here
        } catch (e: IllegalArgumentException) {
            assert(e.message!!.contains("User with id"))
        }
    }
}
