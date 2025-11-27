package net.thechance.chat.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users_of_contacts", schema = "chat")
data class ContactUser(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false)
    val phoneNumber: String,

    @Column(nullable = true)
    val imageUrl: String? = null,

    @Column(nullable = false)
    val isDeleted: Boolean,
)
