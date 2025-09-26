package net.thechance.chat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.ManyToMany
import jakarta.persistence.JoinTable
import jakarta.persistence.JoinColumn
import java.util.UUID

@Entity
@Table(name = "chats", schema = "chat")
data class Chat(
    @Id @Column(name = "chat_id",columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "is_group", nullable = false)
    val isGroup: Boolean,

    @ManyToMany
    @JoinTable(
        name = "chat_users",
        schema = "chat",
        joinColumns = [JoinColumn(name = "chat_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val users: Set<ContactUser> = emptySet()
)