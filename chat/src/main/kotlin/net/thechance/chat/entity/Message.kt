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

    @OneToMany(mappedBy = "message", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val images: List<MessageAttachment> = emptyList(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false)
    val chat: Chat
)