package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "deleted_chats", schema = "chat")
data class DeletedChat(
    @Id @Column(columnDefinition = "uuid",name = "chat_id", nullable = false, updatable = false)
    val chatId : UUID,
    @Column(name = "cleanup_status", nullable = false)
    @Enumerated(EnumType.STRING)
    val cleanUpStatus : CleanUpStatus,
    @Column(name="deleted_at")
    val deletedAt: Instant = Instant.now()
)

enum class CleanUpStatus{
    PENDING,
    S3_DELETED_FAILED,
    DATA_CLEANUP_FAILED,
    DELETED
}