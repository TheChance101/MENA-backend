package net.thechance.identity.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.hibernate.validator.constraints.Range
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users", schema = "identity")
data class User(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "phone_number", nullable = false)
    val phoneNumber: String,

    @Column(name = "password", nullable = false)
    val password: String,

    @Column(name = "first_name", nullable = false)
    val firstName: String,

    @Column(name = "last_name", nullable = false)
    val lastName: String,

    @Column(name = "username", nullable = false, unique = true)
    val username: String,

    @Column(name = "image_url", nullable = true, length = 2083)
    val imageUrl: String?,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDate,

    @field:Range(min = Gender.MALE, max = Gender.FEMALE)
    @Column(name = "gender",  nullable = false)
    val gender: Int,

    @Column(name = "last_login_at", nullable = false)
    val lastLoginAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_visit_at", nullable = false)
    val lastVisitAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    val status: Status,

    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false,

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: LocalDateTime? = null,
) {
    object Gender {
        const val MALE = 1L
        const val FEMALE = 2L
    }

    enum class Status {
        ACTIVE,
        BLOCKED
    }
}