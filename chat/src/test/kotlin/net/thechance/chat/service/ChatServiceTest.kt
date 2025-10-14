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
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageImagesRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import java.util.*

class ChatServiceTest {

    private lateinit var messageRepository: MessageRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var messageImagesRepository: MessageImagesRepository
    private lateinit var attachmentStorageService: AttachmentStorageService
    private lateinit var contactUserService: ContactUserService
    private lateinit var contactService: ContactService
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
        contactService = mockk()
        service = ChatService(
            messageRepository,
            messageImagesRepository,
            chatRepository,
            contactUserService,
            attachmentStorageService,
            contactService,
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

    @Test
    fun `getChatById should get chatModel correctly when specific chat exist`() {
        every { chatRepository.findByIdOrNull(chatId) } returns chat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns contact
        val result = service.getChatById(chatId, userId)
        assertThat(result).isEqualTo(chatModel)
    }

    @Test
    fun `getChatById should return chat name equal to other contact names when the other user is in our contact list`() {
        every { chatRepository.findByIdOrNull(chatId) } returns chat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns contact
        val result = service.getChatById(chatId, userId)

        assertThat(result.name).isEqualTo("${contact.firstName} ${contact.lastName}")
    }

    @Test
    fun `getChatById should return chat name equal to other mina user names when the other user is not in our contact list`() {
        every { chatRepository.findByIdOrNull(chatId) } returns chat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns null
        val result = service.getChatById(chatId, userId)

        assertThat(result.name).isEqualTo("${otherUser.firstName} ${otherUser.lastName}")
    }

    @Test
    fun `getChatById should throw NotFoundException when there is no chat available with specific chat id `() {
        every { chatRepository.findByIdOrNull(notAvailableChatId) } returns null
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns null

        assertThrows<NotFoundException> {
            service.getChatById(notAvailableChatId, userId)
        }
    }

    private companion object {
        val chatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-16ba3869610e")
        val userId = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c404c6")
        val notAvailableChatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-87ba3869610e")

        val contact = Contact(
            id = UUID.fromString("73439a0a-adfa-4bf7-86ad-0d66435d5f18"),
            firstName = "Raouf",
            lastName = "kamel",
            phoneNumber = "+967775074564",
            contactOwnerId = userId
        )
        val otherUser = ContactUser(
            id = UUID.fromString("1804d9db-c870-421d-934b-b00528cb5b93"),
            firstName = "osama",
            lastName = "kamel",
            phoneNumber = "+967775074564",
            imageUrl = null
        )
        val meUser = ContactUser(
            id = userId,
            firstName = "omer",
            lastName = "faris",
            phoneNumber = "9647844440001",
            imageUrl = null
        )

        val chatModel = ChatModel(
            name = "Raouf kamel",
            imageUrl = null,
            requesterId = userId,
            id = chatId
        )

        val chat = Chat(
            id = chatId,
            users = mutableSetOf(meUser, otherUser),
        )
    }
}
