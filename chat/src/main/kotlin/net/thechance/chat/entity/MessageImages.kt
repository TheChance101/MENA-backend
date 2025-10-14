package net.thechance.chat.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "message_images", schema = "chat")
data class MessageImages(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val url: String,
)
