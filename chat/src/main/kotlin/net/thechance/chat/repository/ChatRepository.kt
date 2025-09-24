package net.thechance.chat.repository

import net.thechance.chat.entity.Conversation
import net.thechance.chat.service.model.ConversationModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ConversationRepository : JpaRepository<Conversation, UUID> {


    /*
     * Retrieves a conversation by its ID and the requesting user's ID.
     * - Returns conversation details, including the correct display name and image.
     * - For non-group chats, shows the contact name if available, otherwise the user's name.
     * - For group chats, shows the group name and image.
     */
    @Query(
        """
    SELECT new net.thechance.chat.service.model.ConversationModel(
        c.id,
        c.isGroup,
        CASE 
            WHEN c.isGroup = true THEN gc.groupName
            ELSE COALESCE(CONCAT(ct.firstName, ' ', ct.lastName), CONCAT(u.firstName, ' ', u.lastName))
        END,
        CASE 
            WHEN c.isGroup = true THEN gc.groupImageUrl
            ELSE u.imageUrl
        END
    )
    FROM Conversation c
    LEFT JOIN GroupConversation gc ON c.id = gc.conversation.id
    LEFT JOIN ConversationParticipants p ON c.id = p.id.conversationId AND c.isGroup = false
    LEFT JOIN ContactUser u ON p.id.userId = u.id AND c.isGroup = false
    LEFT JOIN Contact ct ON ct.phoneNumber = u.phoneNumber AND ct.contactOwnerId = :userId AND c.isGroup = false
    WHERE c.id = :conversionId AND (c.isGroup = true OR p.id.userId <> :userId)
    """
    )
    fun getConversationById(conversionId: UUID, userId: UUID): ConversationModel?


    /*
     * Finds all non-group conversations that include exactly all users whose IDs are in the participantIds list.
     * - Only returns conversations where the number of participants matches the size of participantIds (no more, no less).
     * - Only considers non-group conversations (c.isGroup = false).
     * - Used to check if a direct chat exists between a specific set of users.
     */
    @Query(
        """
    SELECT new net.thechance.chat.service.model.ConversationModel(
        c.id,
        c.isGroup,
        CASE
            WHEN c.isGroup = true THEN gc.groupName
            ELSE COALESCE(CONCAT(ct.firstName, ' ', ct.lastName), CONCAT(u.firstName, ' ', u.lastName))
        END,
        CASE
            WHEN c.isGroup = true THEN gc.groupImageUrl
            ELSE u.imageUrl
        END
    )
    FROM Conversation c
    LEFT JOIN GroupConversation gc ON c.id = gc.conversation.id
    JOIN ConversationParticipants p ON c.id = p.id.conversationId
    LEFT JOIN ContactUser u ON p.id.userId = u.id AND c.isGroup = false
    LEFT JOIN Contact ct ON ct.phoneNumber = u.phoneNumber AND c.isGroup = false
    WHERE p.id.userId IN :participantIds AND c.isGroup = false
    GROUP BY c.id, c.isGroup, gc.groupName, gc.groupImageUrl, u.firstName, u.lastName, u.imageUrl, ct.firstName, ct.lastName
    HAVING COUNT(p.id.userId) = :#{#participantIds.size}
    """
    )
    fun getConversationByParticipants(participantIds: List<UUID>): ConversationModel?

}