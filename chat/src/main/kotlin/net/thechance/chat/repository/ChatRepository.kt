package net.thechance.chat.repository

import net.thechance.chat.entity.Conversation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ChatRepository : JpaRepository<Conversation, UUID> {

    fun getConversationById(id: UUID): Conversation?

    /*
    * The conversation includes all users whose IDs are in the participantIds list
    * The number of participants matches the size of the participantIds list
    * The conversation is not a group chat (c.isGroup = false)
    */
    @Query(
        """
        SELECT c FROM Conversation c
        JOIN ConversationParticipants p ON c.id = p.id.conversationId
        WHERE p.id.userId IN :participantIds
        GROUP BY c.id
        HAVING COUNT(p.id.userId) = :#{#participantIds.size}
        AND COUNT(c.id) = :#{#participantIds.size}
        AND c.isGroup = false
    """
    )
    fun getChatByParticipants(participantIds: List<UUID>): Conversation?
}