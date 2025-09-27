package net.thechance.chat.repository

import net.thechance.chat.entity.Chat
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.service.model.ChatModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ChatRepository : JpaRepository<Chat, UUID> {
    fun findByIdIs(id: UUID): Chat?
    fun findByUsersIsAndGroupChatIsNull(users: Set<ContactUser>): Chat?
}