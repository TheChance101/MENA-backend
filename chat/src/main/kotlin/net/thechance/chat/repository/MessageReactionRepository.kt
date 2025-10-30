package net.thechance.chat.repository

import net.thechance.chat.entity.MessageReaction
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MessageReactionRepository: JpaRepository<MessageReaction, UUID> {
    fun findByMessageId(messageId: UUID): List<MessageReaction>
    fun findByMessageIdIn(messageIds: List<UUID>): List<MessageReaction>

    fun deleteByMessageIdAndUserIdAndEmoji(messageId: UUID, userId: UUID, emoji: String)
}