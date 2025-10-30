package net.thechance.chat.repository

import net.thechance.chat.entity.MessageReaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface MessageReactionRepository: JpaRepository<MessageReaction, UUID> {
    fun findByMessageIdIn(messageIds: List<UUID>): List<MessageReaction>

    fun findByMessageIdAndUserId(messageId: UUID, userId: UUID): MessageReaction?

    @Modifying
    @Query("DELETE FROM MessageReaction mr WHERE mr.messageId = :messageId AND mr.userId = :userId")
    fun deleteByMessageIdAndUserId(messageId: UUID, userId: UUID)
}