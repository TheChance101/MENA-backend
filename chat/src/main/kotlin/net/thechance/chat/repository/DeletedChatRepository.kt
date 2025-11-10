package net.thechance.chat.repository

import net.thechance.chat.entity.CleanUpStatus
import net.thechance.chat.entity.DeletedChat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface DeletedChatRepository : JpaRepository<DeletedChat, UUID> {

    fun findAllByCleanUpStatusNot(cleanUpStatus: CleanUpStatus): List<DeletedChat>

    @Query(
        """
        SELECT dc.chatId
        FROM DeletedChat dc
        WHERE dc.userId = :userId AND dc.deletedAt > :time
        """
    )
    fun getDeletedChatsIdByUserIdAfterSpecificTime(userId: UUID, time: Instant): List<UUID>
}