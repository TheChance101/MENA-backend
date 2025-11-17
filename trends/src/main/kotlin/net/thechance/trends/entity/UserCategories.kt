package net.thechance.trends.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Check
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID


@Table(
    name = "user_categories",
    schema = "trends",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "category_id"])],
)
@Entity
@Check(name = "affinity_range", constraints = "affinity >= 0 AND affinity <= 100")
@IdClass(UserCategoryId::class)
data class UserCategories(
    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Id
    @Column(name = "category_id", nullable = false)
    val categoryId: UUID,

    @Column(name = "is_selected", nullable = false)
    val isSelected: Boolean,

    @Column(name = "affinity", nullable = false, )
    val affinity: Int = 0,

    val lastUpdated: LocalDateTime = LocalDateTime.now()
)


data class UserCategoryId(
    val userId: UUID = UUID.randomUUID(),
    val categoryId: UUID = UUID.randomUUID(),
) : Serializable