package net.thechance.chat.repository

import net.thechance.chat.entity.CleanUpStatus
import net.thechance.chat.entity.DeletedChat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface DeletedChatRepository : JpaRepository<DeletedChat, UUID>{

    fun findAllByCleanUpStatusNot(cleanUpStatus: CleanUpStatus) : List<DeletedChat>

    @Query(
        value = """
            SELECT dc.chat_id
            FROM chat.deleted_chats dc
            WHERE dc.user_id = :userId 
            AND dc.deleted_at > :time
        """,
        nativeQuery = true
    )
    fun getDeletedChatsIdByUserIdAfterSpecificTime(userId: UUID, time: Instant): List<UUID>
}
