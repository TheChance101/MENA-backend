package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import net.thechance.identity.entity.User
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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val ownerUser: User
)