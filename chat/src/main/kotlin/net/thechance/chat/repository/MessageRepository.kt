package net.thechance.chat.repository

import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface MessageRepository : JpaRepository<Message, UUID> {

    fun getAllByChatId(chatId: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndIdAfterOrderBySendAtAsc(chatId: UUID, id: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndIdBeforeOrderBySendAtAsc(chatId: UUID, id: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndSendAtAfterOrderBySendAtAsc(chatId: UUID, sendAt: Instant, pageable: Pageable): List<Message>

    fun findAllByChatIdAndReadByUsersNotContaining(chatId: UUID, user: ContactUser, pageable: Pageable): List<Message>

    fun markChatMessagesAsRead(chatId: UUID, user: ContactUser) {
        var page = 0
        val pageSize = PAGE_SIZE
        var messages: List<Message>
        do {
            messages = findAllByChatIdAndReadByUsersNotContaining(chatId, user, Pageable.ofSize(pageSize).withPage(page))
            if (messages.isNotEmpty()) {
                saveAll(messages.onEach { it.readByUsers += user })
            }
            page++
        } while (messages.size == pageSize)
    }

    companion object {
        const val PAGE_SIZE = 500
    }
}