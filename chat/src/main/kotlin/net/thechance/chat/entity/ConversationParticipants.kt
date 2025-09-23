package net.thechance.chat.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "conversation_participant", schema = "chat")
data class ConversationParticipants(
    @EmbeddedId
    val id: ConversationParticipantsId,
)

@Embeddable
data class ConversationParticipantsId(
    @Column(name = "conversation_id", nullable = false)
    val conversationId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,
)