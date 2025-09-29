package net.thechance.chat.repository

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

interface MessageRepository : JpaRepository<Message, UUID> {

    fun getAllByChatId(chatId: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndIdAfterOrderBySentAtAsc(chatId: UUID, id: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndIdBeforeOrderBySentAtAsc(chatId: UUID, id: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndSentAtAfterOrderBySentAtAsc(chatId: UUID, sentAt: Instant, pageable: Pageable): List<Message>

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat.id = :chatId AND m.senderId <> :userId AND m.isRead = false")
    fun updateIsReadByChatIdAndSenderIdNot(chatId: UUID, userId: UUID): Int
}