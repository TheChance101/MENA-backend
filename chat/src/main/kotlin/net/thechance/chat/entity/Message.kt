package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "messages", schema = "chat")
data class Message(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "sender_id", nullable = false)
    val senderId: UUID,
    @Column(name = "conversation_id", nullable = false)
    val conversationId: UUID,
    @Column(name = "content", nullable = false)
    val content: String,
    @Column(name= "timestamp",nullable = false)
    val timestamp: Instant = Instant.now(),
)

