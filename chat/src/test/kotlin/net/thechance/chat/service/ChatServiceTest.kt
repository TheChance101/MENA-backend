package net.thechance.chat.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import net.thechance.chat.api.dto.TextMessageRequestDto
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.Contact
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.ChatRepository
import net.thechance.chat.repository.DeletedChatRepository
import net.thechance.chat.repository.MessageReactionRepository
import net.thechance.chat.repository.MessageRepository
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.service.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.*

class ChatServiceTest {

    private lateinit var messageRepository: MessageRepository
    private lateinit var chatRepository: ChatRepository
    private lateinit var messageReactionRepository: MessageReactionRepository
    private lateinit var attachmentStorageService: AttachmentStorageService
    private lateinit var deletedChatRepository: DeletedChatRepository
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
        users = mutableSetOf()
    )

    @BeforeEach
    fun setUp() {
        messageRepository = mockk(relaxed = true)
        chatRepository = mockk(relaxed = true)
        messageReactionRepository = mockk(relaxed = true)
        deletedChatRepository = mockk(relaxed = true)
        contactUserService = mockk(relaxed = true)
        attachmentStorageService = mockk(relaxed = true)
        entityManager = mockk(relaxed = true)
        contactService = mockk(relaxed = true)
        otherUser = testUser()

        service = ChatService(
            messageRepository,
            messageReactionRepository,
            chatRepository,
            deletedChatRepository,
            contactUserService,
            attachmentStorageService,
            contactService
        )
    }

    @Test
    fun `getChatByUserIds returns existing chat if found`() {
        val requester = testUser2
        val theOtherUser = testUser1
        val chat = Chat(chatId, mutableSetOf(requester, theOtherUser))


        every { contactUserService.getUserById(requester.id) } returns requester
        every { contactUserService.getUserById(theOtherUser.id) } returns theOtherUser
        every { contactService.getContactByOwnerIdAndContactUserId(requester.id, theOtherUser.id) } returns testContact
        every { chatRepository.findByUsersIds(setOf(requester.id, theOtherUser.id)) } returns chat
        val result = service.getChatByUserIds(requester.id, theOtherUser.id)

        assertThat(
            chat.toModel(
                chatName = "${testContact.firstName} ${testContact.lastName}",
                imageUrl = theOtherUser.imageUrl.orEmpty(),
                requesterId = requester.id
            )
        ).isEqualTo(result)
    }

    @Test
    fun `getChatByUserIds creates and returns new chat if not found`() {
        val requester = testUser2
        val theOtherUser = testUser1
        val newChat = Chat(chatId, mutableSetOf(testUser1, testUser2))

        every { chatRepository.findByUsersIds(setOf(requester.id, theOtherUser.id)) } returns null
        every { contactUserService.getUserById(requester.id) } returns requester
        every { contactUserService.getUserById(theOtherUser.id) } returns theOtherUser
        every { chatRepository.save(any()) } returns newChat
        every { contactService.getContactByOwnerIdAndContactUserId(requester.id, theOtherUser.id) } returns testContact

        val result = service.getChatByUserIds(requester.id, theOtherUser.id)

        assertThat(
            newChat.toModel(
                chatName = "${testContact.firstName} ${testContact.lastName}",
                imageUrl = theOtherUser.imageUrl.orEmpty(),
                requesterId = requester.id
            )
        ).isEqualTo(result)
        verify { chatRepository.save(any()) }
    }

    @Test
    fun `saveMessage saves message when chat exists`() {
        val chat = testChat()
        val messageDto = TextMessageRequestDto(
            UUID.randomUUID(),
            chatId = chat.id,
            text = "message 1"
        )

        every { messageRepository.findById(any()) } answers { Optional.empty() }
        every { messageRepository.save(any()) } answers { firstArg<Message>() }

        service.saveTextMessage(MessageRequestArgs(UUID.randomUUID(), chat.id, UUID.randomUUID(), messageDto.text))

        verify {
            messageRepository.save(
                withArg {
                    assertThat(it.chatId).isEqualTo(chat.id)
                    assertThat(it.content).isEqualTo(MessageContent.Text("message 1"))
                }
            )
        }
    }

    @Test
    fun `saveMessageImages should upload image & create message then return message with images urls`() {
        val chat = testChat()
        val senderId = UUID.randomUUID()
        val image = mockk<MultipartFile>(relaxed = true)
        every { entityManager.getReference(Chat::class.java, chat.id) } returns chat
        every { messageRepository.save(any()) } answers { firstArg() }

        service.saveMessageImage(
            MessageImageRequestArgs(
                chatId = chat.id,
                senderId = senderId,
                image = image,
                messageId = UUID.randomUUID()
            )
        )

        verify {
            messageRepository.save(
                withArg {
                    assertThat(it.chatId).isEqualTo(chat.id)
                    assertThat(it.senderId).isEqualTo(senderId)
                }
            )
        }
    }

    @Test
    fun `markChatMessagesAsRead updates messages`() {
        val chatId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        every { messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId, userId) } returns 1

        service.markChatMessagesAsRead(chatId, userId)

        verify { messageRepository.updateIsReadByChatIdAndSenderIdNot(chatId, userId) }
    }

    @Test
    fun `getChatById should get chatModel correctly when specific chat exist`() {
        every { chatRepository.findByIdOrNull(testChat.id) } returns testChat
        every { contactService.getContactByOwnerIdAndContactUserId(testUser2.id, testUser1.id) } returns testContact

        val result = service.getChatById(testChat.id, testUser2.id)

        assertThat(result).isEqualTo(testChatModel)
    }


    @Test
    fun `getChatById should return chat name equal to other contact names when the other user is in our contact list`() {
        every { chatRepository.findByIdOrNull(chatId) } returns testChat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, testUser1.id) } returns testContact
        val result = service.getChatById(chatId, userId)

        assertThat(result.name).isEqualTo("${testContact.firstName} ${testContact.lastName}")
    }

    @Test
    fun `getChatById should return chat name equal to other mina user names when the other user is not in our contact list`() {
        every { chatRepository.findByIdOrNull(chatId) } returns testChat
        every { contactService.getContactByOwnerIdAndContactUserId(userId, testUser1.id) } returns null
        val result = service.getChatById(chatId, userId)

        assertThat(result.name).isEqualTo("${testUser1.firstName} ${testUser1.lastName}")
    }

    @Test
    fun `getChatById should throw NotFoundException when there is no chat available with specific chat id `() {
        every { chatRepository.findByIdOrNull(notAvailableChatId) } returns null
        every { contactService.getContactByOwnerIdAndContactUserId(userId, testUser1.id) } returns null

        assertThrows<NotFoundException> {
            service.getChatById(notAvailableChatId, userId)
        }
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
            type = Message.MessageType.TEXT,
            content = MessageContent.Text(lastMessageText),
            sentAt = Instant.now(),
            senderId = otherUser.id,
            chatId = chat.id,
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
    fun `saveMessageAudio should upload audio and return message with audio url`() {
        val chat = testChat()
        val senderId = UUID.randomUUID()
        val audio = mockk<MultipartFile>(relaxed = true)
        val uploadedAudioUrl = "https://www.example.com/audio/test.m4a"

        every { entityManager.getReference(Chat::class.java, chat.id) } returns chat
        every { attachmentStorageService.uploadAudio(audio, any(), any()) } returns uploadedAudioUrl
        every { messageRepository.save(any()) } answers { firstArg() }

        val result = service.saveMessageAudio(
            net.thechance.chat.service.model.MessageAudioRequestArgs(UUID.randomUUID(), chat.id, senderId, audio, 1000L)
        )

        assertThat(result.content).isEqualTo(MessageContent.Audio(uploadedAudioUrl, 1000L))

    }


    @Test
    fun `saveMessageAudio should throw exception when upload fails`() {
        val chat = testChat()
        val senderId = UUID.randomUUID()
        val audio = mockk<MultipartFile>(relaxed = true)

        every { entityManager.getReference(Chat::class.java, chat.id) } returns chat
        every { attachmentStorageService.uploadAudio(audio, any(), any()) } throws RuntimeException("Upload failed")

        assertThrows<RuntimeException> {
            service.saveMessageAudio(
                net.thechance.chat.service.model.MessageAudioRequestArgs(
                    UUID.randomUUID(),
                    chat.id,
                    senderId,
                    audio,
                    1000L
                )
            )
        }
    }

    @Test
    fun `getUserChats marks chat as mine when last message is from current user`() {
        setupChatEnvironment()
        val myMessage = Message(
            id = UUID.randomUUID(),
            type = Message.MessageType.TEXT,
            content = MessageContent.Text("My message"),
            sentAt = Instant.now(),
            senderId = userId,
            chatId = chat.id,
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

    private companion object {
        val chatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-16ba3869610e")
        val userId = UUID.fromString("451e4d6c-0380-41ed-95e6-275793c404c6")
        val notAvailableChatId = UUID.fromString("825265f7-7e30-4ac3-b9fb-87ba3869610e")

        val testContact = Contact(
            id = UUID.fromString("73439a0a-adfa-4bf7-86ad-0d66435d5f18"),
            firstName = "Raouf",
            lastName = "kamel",
            phoneNumber = "+967775074564",
            contactOwnerId = userId
        )
        val testUser1 = ContactUser(
            id = UUID.fromString("1804d9db-c870-421d-934b-b00528cb5b93"),
            firstName = "osama",
            lastName = "kamel",
            phoneNumber = "+967775074564",
            imageUrl = null
        )
        val testUser2 = ContactUser(
            id = userId,
            firstName = "omer",
            lastName = "faris",
            phoneNumber = "9647844440001",
            imageUrl = null
        )

        val testChatModel = ChatModel(
            name = "Raouf kamel",
            imageUrl = null,
            requesterId = userId,
            id = chatId
        )

        val testChat = Chat(
            id = chatId,
            users = mutableSetOf(testUser2, testUser1),
        )
    }
}
