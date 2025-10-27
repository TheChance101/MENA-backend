package net.thechance.chat.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "messages", schema = "chat")
data class Message(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val senderId: UUID,
    @Column(columnDefinition = "TEXT", nullable = true)
    val text: String? = null,
    @Column(nullable = false)
    val sentAt: Instant = Instant.now(),
    @Column(nullable = false)
    val isRead: Boolean = false,
    @Column(name = "image_url", nullable = true)
    val imageUrl: String? = null,

    @Column(name = "chat_id", columnDefinition = "uuid", nullable = false, updatable = false)
    val chatId: UUID
)