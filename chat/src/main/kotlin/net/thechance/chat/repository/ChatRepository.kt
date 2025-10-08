package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID
import org.springframework.data.domain.Pageable

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
    fun findAllByUserId(userId: UUID, pageable: Pageable): List<Chat>
    @Query("SELECT COUNT(c) FROM Chat c JOIN c.users u WHERE u.id = :userId")
    fun countByUserId(userId: UUID): Long

}

