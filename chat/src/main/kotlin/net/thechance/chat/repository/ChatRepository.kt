package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ChatUnreadMessagesCount
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
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

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<Chat>

    @Query("SELECT COUNT(c) FROM Chat c JOIN c.users u WHERE u.id = :userId")
    fun countByUserId(userId: UUID): Long

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

    @Query(
        nativeQuery = true,
        value = """
        SELECT
            m.chat_id AS chatId,
            COUNT(*) AS unreadCount
        FROM chat.messages m
        WHERE m.is_read = FALSE
          AND m.chat_id = :chatId
        GROUP BY m.chat_id
    """
    )
    fun findUnreadCountForChat(
        @Param("chatId") chatId: UUID
    ): ChatUnreadMessagesCount
}

