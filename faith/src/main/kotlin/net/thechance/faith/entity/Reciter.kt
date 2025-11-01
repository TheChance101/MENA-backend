package net.thechance.faith.entity

import jakarta.persistence.*

@Entity
@Table(name = "reciters", schema = "faith")
data class Reciter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    val id: Int = 0,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val arabicName: String,
    @Column(nullable = false)
    val tilawahType: String,
    @Column(nullable = false)
    val serverUrl: String,
)
