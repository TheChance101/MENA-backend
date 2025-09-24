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
                m.conversationId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= COUNT(cp.id.userId) - 1)
            )
            FROM Message m
            LEFT JOIN MessageSeen ms ON ms.id.messageId = m.id
            LEFT JOIN ConversationParticipants cp ON cp.id.conversationId = m.conversationId
            WHERE m.conversationId = :conversationId
            GROUP BY m.id, m.senderId, m.conversationId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByConversationId(conversationId: UUID, pageable: Pageable): List<MessageModel>

    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.conversationId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= COUNT(cp.id.userId) - 1)
            )
            FROM Message m
            LEFT JOIN MessageSeen ms ON ms.id.messageId = m.id
            LEFT JOIN ConversationParticipants cp ON cp.id.conversationId = m.conversationId
            WHERE m.conversationId = :conversationId AND m.id > :id
            GROUP BY m.id, m.senderId, m.conversationId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByConversationIdAndIdAfterOrderBySendAtAsc(
        conversationId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<MessageModel>

    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.conversationId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= COUNT(cp.id.userId) - 1)
            )
            FROM Message m
            LEFT JOIN MessageSeen ms ON ms.id.messageId = m.id
            LEFT JOIN ConversationParticipants cp ON cp.id.conversationId = m.conversationId
            WHERE m.conversationId = :conversationId AND m.id < :id
            GROUP BY m.id, m.senderId, m.conversationId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByConversationIdAndIdBeforeOrderBySendAtAsc(
        conversationId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<MessageModel>

    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.conversationId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= COUNT(cp.id.userId) - 1)
            )
            FROM Message m
            LEFT JOIN MessageSeen ms ON ms.id.messageId = m.id
            LEFT JOIN ConversationParticipants cp ON cp.id.conversationId = m.conversationId
            WHERE m.conversationId = :conversationId AND m.sendAt > :sendAt
            GROUP BY m.id, m.senderId, m.conversationId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
        """
    )
    fun getAllByConversationIdAndSendAtAfterOrderBySendAtAsc(
        conversationId: UUID,
        sendAt: Instant,
        pageable: Pageable
    ): List<MessageModel>


    @Modifying
    @Query(
        value = """
            INSERT INTO chat.message_seen (user_id, message_id)
                SELECT :userId, m.id
                FROM chat.messages m
                LEFT JOIN chat.message_seen ms ON ms.message_id = m.id AND ms.user_id = :userId
                WHERE m.conversation_id = :conversationId AND ms.message_id IS NULL AND m.sender_id <> :userId
        """,
        nativeQuery = true
    )
    fun markConversationMessagesAsSeen(conversationId: UUID, userId: UUID)


    @Query(
        value = """
            SELECT new net.thechance.chat.service.model.MessageModel(
                m.id,
                m.senderId,
                m.conversationId,
                m.text,
                m.sendAt,
                (COUNT(ms.id.userId) >= COUNT(cp.id.userId) - 1)
            )
            FROM Message m
            LEFT JOIN MessageSeen ms ON ms.id.messageId = m.id
            LEFT JOIN ConversationParticipants cp ON cp.id.conversationId = m.conversationId
            WHERE m.conversationId = :conversationId
            GROUP BY m.id, m.senderId, m.conversationId, m.text, m.sendAt
            ORDER BY m.sendAt ASC
            """,
    )
    fun getAllByConversationIdWithReadStatus(conversationId: UUID, pageable: Pageable): List<MessageModel>

}