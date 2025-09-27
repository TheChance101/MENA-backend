package net.thechance.chat.repository

import net.thechance.chat.entity.ContactUser
import net.thechance.chat.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface MessageRepository : JpaRepository<Message, UUID> {

    fun getAllByChatId(chatId: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndIdAfterOrderBySendAtAsc(chatId: UUID, id: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndIdBeforeOrderBySendAtAsc(chatId: UUID, id: UUID, pageable: Pageable): List<Message>

    fun getAllByChatIdAndSendAtAfterOrderBySendAtAsc(chatId: UUID, sendAt: Instant, pageable: Pageable): List<Message>

    fun findAllByChatIdAndReadByUsersNotContainingAndSenderIdNot(chatId: UUID, user: ContactUser, senderId: UUID, pageable: Pageable): List<Message>
}