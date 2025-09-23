package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Entity
@Table(name = "conversation_participant", schema = "chat", uniqueConstraints = [
    UniqueConstraint(columnNames = ["conversation_id","user_id"])
])
data class ConversationParticipants (
    @Id @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private val id: UUID= UUID.randomUUID(),
    @Column(name = "conversation_id", nullable = false)
    private val conversationId : UUID,
    @Column(name = "user_id", nullable = false)
    private val userId: UUID
)