package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import net.thechance.chat.service.model.ChatModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ChatRepository : JpaRepository<Chat, UUID> {


    /*
     * Retrieves a chat by its ID and the requesting user's ID.
     * - Returns chat details, including the correct display name and image.
     * - For non-group chats, shows the contact name if available, otherwise the user's name.
     * - For group chats, shows the group name and image.
     */
    @Query(
        """
    SELECT new net.thechance.chat.service.model.ChatModel(
        c.id,
        c.isGroup,
        CASE 
            WHEN c.isGroup = true THEN c.groupChat.groupName
            ELSE COALESCE(CONCAT(ct.firstName, ' ', ct.lastName), CONCAT(u.firstName, ' ', u.lastName))
        END,
        CASE 
            WHEN c.isGroup = true THEN c.groupChat.groupImageUrl
            ELSE u.imageUrl
        END
    )
    FROM Chat c
    LEFT JOIN ChatParticipants p ON c.id = p.id.chatId AND c.isGroup = false
    LEFT JOIN ContactUser u ON p.id.userId = u.id AND c.isGroup = false
    LEFT JOIN Contact ct ON ct.phoneNumber = u.phoneNumber AND ct.contactOwnerId = :userId AND c.isGroup = false
    WHERE c.id = :conversionId AND (c.isGroup = true OR p.id.userId <> :userId)
    """
    )
    fun getChatById(conversionId: UUID, userId: UUID): ChatModel?


    /*
     * Finds all non-group chats that include exactly all users whose IDs are in the participantIds list.
     * - Only returns chats where the number of participants matches the size of participantIds (no more, no less).
     * - Only considers non-group chats (c.isGroup = false).
     * - Used to check if a direct chat exists between a specific set of users.
     * - Ensures all those participants are exactly the ones in the provided list.
     */
    @Query(
        """
    SELECT new net.thechance.chat.service.model.ChatModel(
        c.id,
        c.isGroup,
        CASE
            WHEN c.isGroup = true THEN c.groupChat.groupName
            ELSE COALESCE(CONCAT(ct.firstName, ' ', ct.lastName), CONCAT(u.firstName, ' ', u.lastName))
        END,
        CASE
            WHEN c.isGroup = true THEN c.groupChat.groupImageUrl
            ELSE u.imageUrl
        END
    )
    FROM Chat c
    JOIN ChatParticipants p ON c.id = p.id.chatId
    LEFT JOIN ContactUser u ON p.id.userId = u.id AND c.isGroup = false
    LEFT JOIN Contact ct ON ct.phoneNumber = u.phoneNumber AND c.isGroup = false
    WHERE c.isGroup = :isGroup
    GROUP BY c.id, c.isGroup, c.groupChat.groupName, c.groupChat.groupImageUrl, u.firstName, u.lastName, u.imageUrl, ct.firstName, ct.lastName
    HAVING COUNT(DISTINCT p.id.userId) = :#{#participantIds.size}
        AND COUNT(DISTINCT (CASE WHEN p.id.userId IN :participantIds THEN p.id.userId END)) = :#{#participantIds.size}
    """
    )
    fun getChatByParticipants(participantIds: List<UUID>, isGroup: Boolean): ChatModel?

}