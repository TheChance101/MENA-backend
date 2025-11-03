package net.thechance.chat.repository

import net.thechance.chat.entity.MessageReaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface MessageReactionRepository : JpaRepository<MessageReaction, UUID> {
    fun findByMessageIdAndUserId(messageId: UUID, userId: UUID): MessageReaction?

    @Transactional
    fun deleteByMessageIdAndUserId(messageId: UUID, userId: UUID): MessageReaction? {
        val reaction = findByMessageIdAndUserId(messageId, userId)
        if (reaction != null) {
            delete(reaction)
        }
        return reaction
    }
}