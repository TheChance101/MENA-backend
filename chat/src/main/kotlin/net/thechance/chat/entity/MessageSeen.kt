package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "message_reed", schema = "chat")
open class MessageReed(
    @EmbeddedId
    val id: MessageReedId,
)

@Embeddable
open class MessageReedId(
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "message_id", nullable = false)
    val messageId: UUID,
)