package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "messages", schema = "chat")
data class Message(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val senderId: UUID,
    @Column(nullable = false)
    val text: String,
    @Column(nullable = false)
    val sentAt: Instant = Instant.now(),
    @Column(nullable = false)
    val isRead: Boolean,

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false)
    val chat: Chat
)