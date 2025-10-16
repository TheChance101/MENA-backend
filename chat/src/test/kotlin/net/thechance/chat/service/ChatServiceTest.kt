package net.thechance.chat.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityNotFoundException
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.args.CreateMessageArgs
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.ChatModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import java.util.*

class ChatServiceTest {

    private lateinit var messageRepository: MessageRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var contactUserService: ContactUserService
    private lateinit var contactService: ContactService
    private lateinit var entityManager: EntityManager
    private lateinit var service: ChatService

    private lateinit var otherUser: ContactUser
    private lateinit var chat: Chat
    private lateinit var pageable: Pageable
    private lateinit var message: Message
    private lateinit var contact: Contact

    val chatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-16ba3869610e")
    var userId = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c404c6")
    val notAvailableChatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-87ba3869610e")

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
        contactService = mockk(relaxed = true)

        service = ChatService(
            messageRepository,
            chatRepository,
            contactUserService,
            entityManager,
            contactService
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

        assertThat(result).isEqualTo(chat)
        verify { chatRepository.findByUsersIds(setOf(requester.id, theOtherUser.id)) }
        verify(exactly = 0) { chatRepository.save(any()) }
    }

    @Test
    fun `getOrCreateConversationByParticipants creates new chat when not found`() {
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

        assertThat(result).isEqualTo(newChat)
        verify { chatRepository.save(any()) }
    }

    @Test
    fun `saveMessage saves message when chat exists`() {
        val chat = testChat()
        val messageDto = CreateMessageArgs(
            chatId = chat.id,
            senderId = UUID.randomUUID(),
            text = "message 1",
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
    fun `saveMessage throws exception when chat not found`() {
        val messageDto = CreateMessageArgs(
            chatId = UUID.randomUUID(),
            senderId = UUID.randomUUID(),
            text = "message 1",
        )

        every { entityManager.getReference(Chat::class.java, messageDto.chatId) } throws EntityNotFoundException()

        assertThrows<EntityNotFoundException> {
            service.saveMessage(messageDto)
        }
    }

    @Test
    fun `markChatMessagesAsRead updates messages as read`() {
        val chatId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        service.markChatMessagesAsRead(chatId, userId)

        verify { messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId, userId) }
    }

    @Test
    fun `getChatById should get chatModel correctly when specific chat exist`() {
        setupChatEnvironment()
        every { chatRepository.findByIdOrNull(chatId) } returns chat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns contact
        val result = service.getChatById(chatId, userId)
        assertThat(result).isEqualTo(chatModel)
    }

    @Test
    fun `getChatById should return chat name equal to other contact names when the other user is in our contact list`() {
        setupChatEnvironment()
        every { chatRepository.findByIdOrNull(chatId) } returns chat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns contact
        val result = service.getChatById(chatId, userId)

        assertThat(result.name).isEqualTo("${contact.firstName} ${contact.lastName}")
    }

    @Test
    fun `getChatById should return chat name equal to other mina user names when the other user is not in our contact list`() {
        setupChatEnvironment()
        every { chatRepository.findByIdOrNull(chatId) } returns chat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns null
        val result = service.getChatById(chatId, userId)

        assertThat(result.name).isEqualTo("${otherUser.firstName} ${otherUser.lastName}")
    }

    @Test
    fun `getChatById should throw NotFoundException when there is no chat available with specific chat id `() {
        setupChatEnvironment()
        every { chatRepository.findByIdOrNull(notAvailableChatId) } returns null
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns null

        assertThrows<NotFoundException> {
            service.getChatById(notAvailableChatId, userId)
        }
    }

    private companion object {
        val chatId: UUID = UUID.fromString("825265f7-7e30-4ac3-b9fb-16ba3869610e")
        val userId: UUID = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c404c6")

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
            name = "Ali Ahmed",
            imageUrl = null,
            requesterId = userId,
            id = chatId
        )

        val chat = Chat(
            id = chatId,
            users = mutableSetOf(meUser, otherUser),
        )
    }

    private fun setupChatEnvironment() {
        userId = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c404c6") //UUID.randomUUID()
        otherUser = testUser()
        chat = testChat().apply { users.addAll(listOf(testUser(userId), otherUser)) }
        pageable = Pageable.unpaged()

        contact = mockk<Contact>().apply {
            every { firstName } returns "Ali"
            every { lastName } returns "Ahmed"
        }
    }

    private fun mockChatDependencies(
        unreadCount: Long = 2L,
        lastMessageText: String = "Hello"
    ) {
        message = Message(
            id = UUID.randomUUID(),
            text = lastMessageText,
            sentAt = Instant.now(),
            senderId = otherUser.id,
            chat = chat,
            isRead = false
        )

        contact = mockk<Contact>().apply {
            every { firstName } returns "Ali"
            every { lastName } returns "Ahmed"
        }

        val chatPage = PageImpl(listOf(chat))
        every { chatRepository.findAllByUserId(userId, pageable) } returns chatPage
        every { messageRepository.findLastMessagesForChats(listOf(chat.id)) } returns listOf(message)
        every { chatRepository.findUnreadCountsForChats(listOf(chat.id)) } returns listOf(
            mockk {
                every { chatId } returns chat.id
                every { unreadCount } returns unreadCount
            }
        )
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns contact
    }



    @Test
    fun `getUserChats marks chat as mine when last message is from current user`() {
        setupChatEnvironment()
        val myMessage = Message(
            id = UUID.randomUUID(),
            text = "My message",
            sentAt = Instant.now(),
            senderId = userId,
            chat = chat,
            isRead = false
        )

        val chatPage = PageImpl(listOf(chat))
        every { chatRepository.findAllByUserId(userId, pageable) } returns chatPage
        every { messageRepository.findLastMessagesForChats(listOf(chat.id)) } returns listOf(myMessage)
        every { chatRepository.findUnreadCountsForChats(listOf(chat.id)) } returns listOf(
            mockk {
                every { chatId } returns chat.id
                every { unreadCount } returns 0
            }
        )
        every { contactService.getContactByOwnerIdAndContactUserId(userId, otherUser.id) } returns contact

        val result = service.getUserChatsSummaries(userId, pageable)
        val firstChat = result.content.first()

        assertThat(firstChat.lastMessage?.isMine).isTrue()
    }


    @Test
    fun `getUserChats handles empty chat list correctly`() {
        setupChatEnvironment()
        val emptyChatPage = PageImpl<Chat>(emptyList())

        every { chatRepository.findAllByUserId(userId, pageable) } returns emptyChatPage
        every { messageRepository.findLastMessagesForChats(emptyList()) } returns emptyList()
        every { chatRepository.findUnreadCountsForChats(emptyList()) } returns emptyList()

        val result = service.getUserChatsSummaries(userId, pageable)

        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(0)
    }
}