package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.ManyToMany
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "messages", schema = "chat")
data class Message(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "sender_id", nullable = false)
    val senderId: UUID,
    @Column(name = "chat_id", nullable = false)
    val chatId: UUID,
    @Column(name = "text", nullable = false)
    val text: String,
    @Column(name= "sendAt",nullable = false)
    val sendAt: Instant = Instant.now(),

    @ManyToMany(mappedBy = "seenMessages")
    val seenByUsers: Set<ContactUser> = emptySet()
)