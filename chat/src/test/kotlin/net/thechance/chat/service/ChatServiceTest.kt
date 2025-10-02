package net.thechance.chat.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityNotFoundException
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.*

class ChatServiceTest {

    private lateinit var messageRepository: MessageRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var contactUserService: ContactUserService
    private lateinit var entityManager: EntityManager
    private lateinit var service: ChatService

    private fun testUser(id: UUID = UUID.randomUUID()) = ContactUser(
        id = id,
        firstName = "Test",
        lastName = "User",
        phoneNumber = "123456789"
    )

    private fun testChat(id: UUID = UUID.randomUUID()) = Chat(
        id = id,
        users = mutableSetOf(),
        messages = mutableSetOf()
    )

    @BeforeEach
    fun setUp() {
        messageRepository = mockk(relaxed = true)
        chatRepository = mockk(relaxed = true)
        contactUserService = mockk(relaxed = true)
        entityManager = mockk(relaxed = true)
        service = ChatService(messageRepository, chatRepository, contactUserService, entityManager)
    }

    @Test
    fun `getOrCreateConversationByParticipants returns existing chat if found`() {
        val requester = testUser()
        val theOtherUser = testUser()
        val chat = testChat().apply { users.addAll(listOf(requester, theOtherUser)) }

        every { entityManager.getReference(ContactUser::class.java, requester.id) } returns requester
        every { entityManager.getReference(ContactUser::class.java, theOtherUser.id) } returns theOtherUser
        every { chatRepository.findByUsersIds(setOf(requester.id, theOtherUser.id)) } returns chat

        val result = service.getOrCreateConversationByParticipants(requester.id, theOtherUser.id)

        assertThat(chat).isEqualTo(result)
        verify { chatRepository.findByUsersIds(setOf(requester.id, theOtherUser.id)) }
        verify(exactly = 0) { chatRepository.save(any()) }
    }

    @Test
    fun `getOrCreateConversationByParticipants creates and returns new chat if not found`() {
        val requester = testUser()
        val theOtherUser = testUser()
        val newChat = testChat().apply { users.addAll(listOf(requester, theOtherUser)) }

        every { entityManager.getReference(ContactUser::class.java, requester.id) } returns requester
        every { entityManager.getReference(ContactUser::class.java, theOtherUser.id) } returns theOtherUser
        every { chatRepository.findByUsersIds(setOf(requester.id, theOtherUser.id)) } returns null
        every { contactUserService.getUserById(requester.id) } returns requester
        every { contactUserService.getUserById(theOtherUser.id) } returns theOtherUser
        every { chatRepository.save(any()) } returns newChat

        val result = service.getOrCreateConversationByParticipants(requester.id, theOtherUser.id)

        assertThat(newChat).isEqualTo(result)
        verify { chatRepository.save(any()) }
    }

    @Test
    fun `saveMessage saves message when chat exists`() {
        val chat = testChat()
        val messageDto = CreateMessageArgs(
            id = UUID.randomUUID(),
            chatId = chat.id,
            senderId = UUID.randomUUID(),
            text = "message 1",
            sendAt = Instant.now(),
            isRead = false
        )

        every { entityManager.getReference(Chat::class.java, chat.id) } returns chat
        every { messageRepository.save(any()) } answers { firstArg() }

        service.saveMessage(messageDto)

        verify {
            messageRepository.save(
                withArg {
                    assertThat(it.chat).isEqualTo(chat)
                    assertThat(it.text).isEqualTo("message 1")
                }
            )
        }
    }

    @Test
    fun `saveMessage throws if chat not found`() {
        val messageDto = CreateMessageArgs(
            id = UUID.randomUUID(),
            chatId = UUID.randomUUID(),
            senderId = UUID.randomUUID(),
            text = "message 1",
            sendAt = Instant.now(),
            isRead = false
        )

        every { entityManager.getReference(Chat::class.java, messageDto.chatId) } throws EntityNotFoundException()

        assertThrows<EntityNotFoundException> {
            service.saveMessage(messageDto)
        }
    }

    @Test
    fun `markChatMessagesAsRead updates messages`() {
        val chatId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        service.markChatMessagesAsRead(chatId, userId)

        verify { messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId, userId) }
    }
}
