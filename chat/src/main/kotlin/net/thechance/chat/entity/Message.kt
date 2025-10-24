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

    @ElementCollection(targetClass = String::class, fetch = FetchType.EAGER)
    @CollectionTable(name = "message_images", schema = "chat", joinColumns = [JoinColumn(name = "message_id")])
    @Column(name = "url", nullable = false)
    val images: List<String> = emptyList(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false)
    val chat: Chat
)