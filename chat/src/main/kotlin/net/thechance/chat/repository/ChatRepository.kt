package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface ChatRepository : JpaRepository<Chat, UUID> {
    @Query("""
        select c
        from Chat c
        join c.users u
        where u.id in :userIds
        group by c
        having count(c) = :#{#userIds.size}
        """
    )
    fun findPrivateChatBetweenUsers(userIds: Set<UUID>): Chat?
    fun findByUsers(users: Set<ContactUser>): Chat?
}