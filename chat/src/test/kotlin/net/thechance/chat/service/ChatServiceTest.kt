package net.thechance.chat.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.ContactUserRepository
import net.thechance.chat.repository.MessageRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

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
    fun `getOrCreateConversationByParticipants returns existing chat if found`() {
        val requester = testUser()
        val theOtherUser = testUser()
        val chat = testChat().apply { users.addAll(listOf(requester, theOtherUser)) }

        every { contactUserRepository.findById(requester.id) } returns Optional.of(requester)
        every { contactUserRepository.findById(theOtherUser.id) } returns Optional.of(theOtherUser)
        every { chatRepository.findPrivateChatBetweenUsers(setOf(requester.id, theOtherUser.id)) } returns chat

        val result = service.getOrCreateConversationByParticipants(requester.id, theOtherUser.id)

        assertThat(chat).isEqualTo(result)
        verify { chatRepository.findPrivateChatBetweenUsers(setOf(requester.id, theOtherUser.id)) }
        verify(exactly = 0) { chatRepository.save(any()) }
    }

    @Test
    fun `getOrCreateConversationByParticipants creates and returns new chat if not found`() {
        val requester = testUser()
        val theOtherUser = testUser()
        val newChat = testChat().apply { users.addAll(listOf(requester, theOtherUser)) }

        every { contactUserRepository.findById(requester.id) } returns Optional.of(requester)
        every { contactUserRepository.findById(theOtherUser.id) } returns Optional.of(theOtherUser)
        every { chatRepository.findPrivateChatBetweenUsers(setOf(requester.id, theOtherUser.id)) } returns null
        every { chatRepository.save(any()) } returns newChat

        val result = service.getOrCreateConversationByParticipants(requester.id, theOtherUser.id)

        assertThat(newChat).isEqualTo(result)
        verify { chatRepository.save(any()) }
    }

    @Test
    fun `getOrCreateConversationByParticipants throws if requester not found`() {
        val requesterId = UUID.randomUUID()
        val theOtherUser = testUser()
        every { chatRepository.findPrivateChatBetweenUsers(any()) } returns null
        every { contactUserRepository.findById(requesterId) } returns Optional.empty()

        val exception = assertThrows<IllegalArgumentException> {
            service.getOrCreateConversationByParticipants(requesterId, theOtherUser.id)
        }
        assertThat(exception).hasMessageThat().contains("Requester not found")
    }

    @Test
    fun `getOrCreateConversationByParticipants throws if the other user not found`() {
        val requester = testUser()
        val theOtherUserId = UUID.randomUUID()
        every { chatRepository.findPrivateChatBetweenUsers(any()) } returns null
        every { contactUserRepository.findById(requester.id) } returns Optional.of(requester)
        every { contactUserRepository.findById(theOtherUserId) } returns Optional.empty()

        val exception = assertThrows<IllegalArgumentException> {
            service.getOrCreateConversationByParticipants(requester.id, theOtherUserId)
        }
        assertThat(exception).hasMessageThat().contains("Other user not found")
    }
}
