package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "messages", schema = "chat")
data class Message(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "sender_id", nullable = false)
    val senderId: UUID,
    @Column(name = "text", nullable = false)
    val text: String,
    @Column(name= "sendAt",nullable = false)
    val sendAt: Instant = Instant.now(),

    @ManyToMany(mappedBy = "reedMessages")
    val reedByUsers: Set<ContactUser> = emptySet(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", referencedColumnName = "id", nullable = false)
    val chat: Chat
) {
    val isRead: Boolean
        get() = reedByUsers.size >= (chat.users.size - 1)
}