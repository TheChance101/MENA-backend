package net.thechance.chat.entity

import jakarta.persistence.*
import net.thechance.chat.repository.converter.MessageContentConverter
import net.thechance.chat.service.model.MessageContent
import java.time.Instant
import java.util.*

@Entity
@Table(name = "messages", schema = "chat")
data class Message(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    val senderId: UUID,
    @Column(nullable = false)
    val sentAt: Instant = Instant.now(),
    @Column(nullable = false)
    val isRead: Boolean = false,
    @Column(name = "last_modified_at", nullable = false)
    var lastModifiedAt: Instant = Instant.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: MessageType,

    @Convert(converter = MessageContentConverter::class)
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    val content: MessageContent,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    val reactions: List<MessageReaction> = emptyList(),

    @Column(name = "chat_id", columnDefinition = "uuid", nullable = false, updatable = false)
    val chatId: UUID


) {
    enum class MessageType {
        TEXT,
        IMAGE,
        AUDIO,
        ORDER
    }
}