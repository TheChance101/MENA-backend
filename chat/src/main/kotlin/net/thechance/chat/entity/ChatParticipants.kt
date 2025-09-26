package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "chat_participant", schema = "chat")
data class ChatParticipants(
    @EmbeddedId
    val id: ChatParticipantsId,
)

@Embeddable
data class ChatParticipantsId(
    @Column(name = "chat_id", nullable = false)
    val chatId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,
)