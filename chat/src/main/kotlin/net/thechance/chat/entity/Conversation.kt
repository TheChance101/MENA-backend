package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "conversations", schema = "chat")
open class Conversation(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "is_group", nullable = false)
    val isGroup: Boolean
)