package net.thechance.dukan.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Table(name = "dukan_categories", schema = "dukan")
@Entity
data class DukanCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "icon_url", nullable = false)
    val iconUrl: String,
    @Column(name = "arabic_title", nullable = false)
    val arabicTitle: String,
    @Column(name = "english_title", nullable = false)
    val englishTitle: String,
)
