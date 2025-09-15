package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "contacts")
data class Contact(
    @Id @Column(
        columnDefinition = "uuid",
        updatable = false,
        nullable = false
    ) val id: UUID = UUID.randomUUID(),
    @Column(nullable = false) val name: String,
    @Column(nullable = false) val phoneNumber: String,
    @Column(nullable = false) val userId: UUID,

)