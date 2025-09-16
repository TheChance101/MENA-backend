package net.thechance.chat.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "contacts",
    schema = "chat",
    indexes = [Index(name = "idx_contacts_user_id", columnList = "userId")]
)
data class Contact(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(nullable = false) val firstName: String,
    @Column(nullable = false) val lastName: String,
    @Column(nullable = false) val phoneNumber: String,
    @Column(nullable = false) val userId: UUID,
)