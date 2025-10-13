package net.thechance.chat.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityNotFoundException
import net.thechance.chat.api.dto.MessageImagesRequestDto
import net.thechance.chat.api.dto.MessageRequestDto
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageImagesRepository
import net.thechance.chat.repository.MessageRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ChatServiceTest {

    private lateinit var messageRepository: MessageRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var messageImagesRepository: MessageImagesRepository
    private lateinit var attachmentStorageService: AttachmentStorageService
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
        messageImagesRepository = mockk(relaxed = true)
        chatRepository = mockk(relaxed = true)
        contactUserService = mockk(relaxed = true)
        attachmentStorageService = mockk(relaxed = true)
        entityManager = mockk(relaxed = true)
        service = ChatService(
            messageRepository,
            messageImagesRepository,
            chatRepository,
            contactUserService,
            attachmentStorageService,
            entityManager
        )
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
        val messageDto = MessageRequestDto(
            chatId = chat.id,
            messageId = UUID.randomUUID(),
            text = "message 1",
        )

        every { entityManager.getReference(Chat::class.java, chat.id) } returns chat
        every { messageRepository.save(any()) } answers { firstArg() }

        service.saveMessage(UUID.randomUUID(), messageDto)

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
        val messageDto = MessageRequestDto(
            chatId = UUID.randomUUID(),
            messageId = UUID.randomUUID(),
            text = "message 1",
        )

        every { entityManager.getReference(Chat::class.java, messageDto.chatId) } throws EntityNotFoundException()

        assertThrows<EntityNotFoundException> {
            service.saveMessage(UUID.randomUUID(), messageDto)
        }
    }

    @Test
    fun `saveMessageImages should upload image & create message then return message with images urls`() {
        val chat = testChat()
        val req = MessageImagesRequestDto(chat.id, emptyList())

        every { entityManager.getReference(Chat::class.java, chat.id) } returns chat
        every { messageRepository.save(any()) } answers { firstArg() }

        service.saveMessageImages(UUID.randomUUID(), req)

        verify {
            messageRepository.save(
                withArg {
                    assertThat(it.chat).isEqualTo(chat)
                    assertThat(it.text).isEqualTo(null)
                }
            )
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
