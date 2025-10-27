package net.thechance.chat.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "chats", schema = "chat")
data class Chat(
    @Id @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "chat_users",
        schema = "chat",
        joinColumns = [JoinColumn(name = "chat_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")]
    )
    val users: MutableSet<ContactUser> = mutableSetOf(),

    @Column(name = "cleanup_status", nullable = false)
    @Enumerated(EnumType.STRING)
    var cleanUpStatus : CleanUpStatus = CleanUpStatus.NONE
){
    fun setCleanUpStatus(cleanUpStatus: CleanUpStatus): Chat{
        this.cleanUpStatus = cleanUpStatus
        return this
    }
}

enum class CleanUpStatus{
    NONE,
    S3_DELETED_FAILED,
    CLEANUP_FAILED,
}
