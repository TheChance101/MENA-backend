package net.thechance.chat.repository

import net.thechance.chat.entity.Message
import net.thechance.chat.service.model.MessageModel
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface MessageRepository : JpaRepository<Message, UUID> {

    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.chatId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= (c.users.size - 1))
            )
            FROM Message m
            LEFT JOIN MessageReed ms ON ms.id.messageId = m.id
            LEFT JOIN Chat c ON c.id = m.chatId
            WHERE m.chatId = :chatId
            GROUP BY m.id, m.senderId, m.chatId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByChatId(chatId: UUID, pageable: Pageable): List<MessageModel>

    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.chatId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= (c.users.size - 1))
            )
            FROM Message m
            LEFT JOIN MessageReed ms ON ms.id.messageId = m.id
            LEFT JOIN Chat c ON c.id = m.chatId
            WHERE m.chatId = :chatId AND m.id > :id
            GROUP BY m.id, m.senderId, m.chatId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByChatIdAndIdAfterOrderBySendAtAsc(
        chatId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<MessageModel>

    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.chatId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= (c.users.size - 1))
            )
            FROM Message m
            LEFT JOIN MessageReed ms ON ms.id.messageId = m.id
            LEFT JOIN Chat c ON c.id = m.chatId
            WHERE m.chatId = :chatId AND m.id < :id
            GROUP BY m.id, m.senderId, m.chatId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByChatIdAndIdBeforeOrderBySendAtAsc(
        chatId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<MessageModel>

    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.chatId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= (c.users.size - 1))
            )
            FROM Message m
            LEFT JOIN MessageReed ms ON ms.id.messageId = m.id
            LEFT JOIN Chat c ON c.id = m.chatId
            WHERE m.chatId = :chatId AND m.sendAt > :sendAt
            GROUP BY m.id, m.senderId, m.chatId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByChatIdAndSendAtAfterOrderBySendAtAsc(
        chatId: UUID,
        sendAt: Instant,
        pageable: Pageable
    ): List<MessageModel>


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