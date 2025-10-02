package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ChatRepository : JpaRepository<Chat, UUID> {
    fun findByUsersIs(users: Set<ContactUser>): Chat?
}