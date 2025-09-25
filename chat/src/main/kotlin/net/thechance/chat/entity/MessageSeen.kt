package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "message_seen", schema = "chat")
open class MessageSeen(
    @EmbeddedId
    val id: MessageSeenId,
)

@Embeddable
open class MessageSeenId(
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "message_id", nullable = false)
    val messageId: UUID,
)