package net.thechance.chat.repository

import net.thechance.chat.entity.MessageReaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface MessageReactionRepository : JpaRepository<MessageReaction, UUID> {
    fun findByMessageIdAndUserId(messageId: UUID, userId: UUID): MessageReaction?

    @Transactional
    fun deleteByMessageIdAndUserId(messageId: UUID, userId: UUID)

    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            DELETE FROM chat.message_reactions mr
            USING chat.messages m
            WHERE mr.message_id = m.id AND m.chat_id = :chatId
        """
    )
    fun deleteAllByChatId(chatId: UUID)
}