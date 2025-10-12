package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

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
}

