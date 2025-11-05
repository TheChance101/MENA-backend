package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "deleted_chats", schema = "chat")
data class DeletedChat(
    @Id @Column(columnDefinition = "uuid",name = "chat_id", nullable = false, updatable = false)
    val chatId : UUID,
    @Column(name = "cleanup_status", nullable = false)
    @Enumerated(EnumType.STRING)
    var cleanUpStatus : CleanUpStatus
)

enum class CleanUpStatus{
    S3_DELETED_FAILED,
    CLEANUP_FAILED,
    //DELETED
}