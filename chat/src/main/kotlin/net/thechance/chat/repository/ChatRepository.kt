package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ChatRepository : JpaRepository<Chat, UUID> {
    fun findByIdIs(id: UUID): Chat?
    fun findByUsers(users: Set<ContactUser>): Chat?

    @Query(
        """
        SELECT c
        FROM Chat c
        JOIN c.users u
        WHERE u.id IN :userIds
        GROUP BY c
        HAVING COUNT(u) = :#{#userIds.size}
            AND SUM(CASE WHEN u.id IN :userIds THEN 1 ELSE 0 END) = :#{#userIds.size}
    """
    )
    fun findByUsersIdsAndGroupChatIsNull(userIds: Set<UUID>): Chat?

    @Query("""
    select c from Chat c
    join c.users u
    where u.id in :userIds
    group by c
    having count(c) = :size
""")
    fun findPrivateChatBetweenUsers(
        @Param("userIds") userIds: Set<UUID>,
        @Param("size") size: Long
    ): Chat?
}