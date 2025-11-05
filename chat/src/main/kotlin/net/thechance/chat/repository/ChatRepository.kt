package net.thechance.chat.repository

import jakarta.transaction.Transactional
import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.CleanUpStatus
import net.thechance.chat.service.model.ChatUnreadMessagesCount
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ChatRepository : JpaRepository<Chat, UUID> {
    @Query(
        """
        SELECT c
        FROM Chat c
        JOIN c.users u
        WHERE u.id in :userIds
        GROUP by c
        HAVING count(u) = :#{#userIds.size}
        """
    )
    fun findByUsersIds(userIds: Set<UUID>): Chat?

    @Query(""" SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId 
        AND NOT EXISTS (SELECT 1 FROM DeletedChat dc WHERE dc.chatId = c.id) """)
    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<Chat>


    @Query(
        nativeQuery = true,
        value = """
        SELECT
            m.chat_id AS chatId,
            COUNT(*) AS unreadCount
        FROM chat.messages m
        WHERE m.is_read = FALSE
          AND m.chat_id IN :chatIds
        GROUP BY m.chat_id
    """
    )
    fun findUnreadCountsForChats(
        @Param("chatIds") chatIds: List<UUID>
    ): List<ChatUnreadMessagesCount>
}

