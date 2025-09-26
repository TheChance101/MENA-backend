package net.thechance.chat.service

import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import net.thechance.chat.repository.MessageRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class messagingService(
    private val messageRepository: MessageRepository,
) {

    @Transactional
    fun markChatMessagesAsRead(chatId: UUID, user: ContactUser) {
        var page = 0
        val pageSize = PAGE_SIZE
        var messages: List<Message>
        do {
            messages = messageRepository.findAllByChatIdAndReadByUsersNotContaining(
                chatId,
                user,
                Pageable.ofSize(pageSize).withPage(page)
            )
            if (messages.isNotEmpty()) {
                messageRepository.saveAll(messages.onEach { it.readByUsers += user })
            }
            page++
        } while (messages.size == pageSize)
    }

    companion object {
        const val PAGE_SIZE = 500
    }
}