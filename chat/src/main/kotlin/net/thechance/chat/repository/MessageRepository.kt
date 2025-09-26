package net.thechance.chat.repository

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

    @Modifying
    @Query(
        value = """
            INSERT INTO chat.message_reed (user_id, message_id)
                SELECT :userId, m.id
                FROM chat.messages m
                LEFT JOIN chat.message_reed ms ON ms.message_id = m.id AND ms.user_id = :userId
                WHERE m.chat_id = :chatId AND ms.message_id IS NULL AND m.sender_id <> :userId
        """,
        nativeQuery = true
    )
    fun markChatMessagesAsReed(chatId: UUID, userId: UUID)
}