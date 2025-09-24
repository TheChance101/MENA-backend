package net.thechance.chat.repository

import net.thechance.chat.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface MessageRepository : JpaRepository<Message, UUID> {

    fun getAllByConversationId(conversationId: UUID, pageable: Pageable): List<Message>

    fun getAllByConversationIdAndIdAndIdAfterOrderBySendAtAsc(
        conversationId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<Message>

    fun getAllByConversationIdAndIdAndIdBeforeOrderBySendAtAsc(
        conversationId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<Message>

    fun getAllByConversationIdAndSendAtAfterOrderBySendAtAsc(
        conversationId: UUID,
        sendAt: Instant,
        pageable: Pageable
    ): List<Message>


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


//    @Query(
//        value = """
//    SELECT
//        m.id, m.sender_id, m.conversation_id, m.text, m.timestamp,
//        (COUNT(ms.user_id) >= COUNT(cp.user_id) - 1) AS is_read
//    FROM Message m
//    LEFT JOIN MessageSeen ms ON ms.id.message_id = m.id
//    LEFT JOIN ConversationParticipants cp ON cp.id.conversation_id = m.conversation_id
//    WHERE m.conversation_id = :conversationId
//    GROUP BY m.id, m.sender_id, m.conversation_id, m.text, m.timestamp
//    ORDER BY m.timestamp ASC
//    """,
//    )
//    fun getAllByConversationIdWithReadStatus(conversationId: UUID): List<Map<String, Any>>

}