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

    fun getAllByConversationIdAndIdAndIdAfterOrderByTimestampAsc(
        conversationId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<Message>

    fun getAllByConversationIdAndIdAndIdBeforeOrderByTimestampAsc(
        conversationId: UUID,
        id: UUID,
        pageable: Pageable
    ): List<Message>

    fun getAllByConversationIdAndTimestampAfterOrderByTimestampAsc(
        conversationId: UUID,
        timestamp: Instant,
        pageable: Pageable
    ): List<Message>


    @Modifying
    @Query(
        value = """
            INSERT INTO chat.message_seen (user_id, message_id)
                SELECT :userId, m.id
                FROM chat.messages m
                LEFT JOIN chat.message_seen ms ON ms.message_id = m.id AND ms.user_id = :userId
                WHERE m.conversation_id = :conversationId AND ms.message_id IS NULL
        """,
        nativeQuery = true
    )
    fun markConversationMessagesAsSeen(conversationId: UUID, userId: UUID)
}