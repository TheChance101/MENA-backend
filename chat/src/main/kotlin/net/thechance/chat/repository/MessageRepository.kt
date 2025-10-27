package net.thechance.chat.repository

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface MessageRepository : JpaRepository<Message, UUID> {

    fun getAllByChatIdOrderBySentAtDesc(chatId: UUID, pageable: Pageable): Page<Message>

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chatId = :chatId AND m.senderId <> :userId AND m.isRead = false")
    fun updateIsReadByChatIdAndSenderIdNot(chatId: UUID, userId: UUID): Int

    fun findTopByChatIdOrderBySentAtDesc(chatId: UUID): Message?
    fun countByChatIdAndSenderIdNotAndIsReadFalse(chatId: UUID, userId: UUID): Int
    fun countByChatIdAndIsReadFalse(chatId: UUID): Int

    @Query(
        nativeQuery = true,
        value = """
        SELECT DISTINCT ON (m.chat_id) 
        m.chat_id, m.id, m.text, m.image_url, m.sender_id, m.sent_at, m.is_read
        FROM chat.messages m
        WHERE m.chat_id IN :chatIds
        ORDER BY m.chat_id, m.sent_at DESC
    """
    )
    fun findLastMessagesForChats(@Param("chatIds") chatIds: List<UUID>): List<Message>

}