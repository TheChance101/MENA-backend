package net.thechance.chat.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "message_reactions",
    schema = "chat",
    uniqueConstraints = [UniqueConstraint(columnNames = ["message_id", "user_id"])]
)
data class MessageReaction(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "message_id", columnDefinition = "uuid", nullable = false)
    val messageId: UUID,

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val emoji: String
)
