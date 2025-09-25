package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "conversation_participant", schema = "chat")
open class ConversationParticipants(
    @EmbeddedId
    val id: ConversationParticipantsId,
)

@Embeddable
open class ConversationParticipantsId(
    @Column(name = "conversation_id", nullable = false)
    val conversationId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,
)