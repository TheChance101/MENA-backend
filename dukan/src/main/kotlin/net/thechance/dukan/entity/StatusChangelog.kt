package net.thechance.dukan.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "status_changelogs")
data class StatusChangelog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val dukanId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: Status,

    @Column(nullable = false)
    val reason: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
){
    enum class Status {
        REJECTED,
        DEACTIVATED
    }
}
