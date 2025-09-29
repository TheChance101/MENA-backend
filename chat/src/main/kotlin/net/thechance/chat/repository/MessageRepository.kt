package net.thechance.chat.repository

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface MessageRepository : JpaRepository<Message, UUID> {

    fun getAllByChatId(chatId: UUID, pageable: Pageable): Page<Message>
    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat.id = :chatId AND m.senderId <> :userId")
    fun updateIsReadByChatIdAndSenderIdNot(chatId: UUID, userId: UUID): Int
}