package net.thechance.dukan.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Table(
    name = "dukan_shelves",
    schema = "dukan",
    uniqueConstraints = [UniqueConstraint(columnNames = ["dukan_id", "title"])]
)
@Entity
data class DukanShelf(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "title", nullable = false)
    val title: String,
    @ManyToOne
    @JoinColumn(name = "dukan_id", nullable = false)
    val dukan: Dukan,
)
