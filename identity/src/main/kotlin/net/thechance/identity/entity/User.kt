package net.thechance.identity.entity

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import net.thechance.identity.mapper.util.DEFAULT_DATE_FORMAT
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "users", schema = "identity")
data class User(
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "phone_number", nullable = false, unique = true)
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DEFAULT_DATE_FORMAT)
    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDate,

    @field:Min(
        value = Gender.MALE.toLong(),
        message = "Gender must be ${Gender.MALE} (man) or ${Gender.FEMALE} (woman)"
    )
    @field:Max(
        value = Gender.FEMALE.toLong(),
        message = "Gender must be ${Gender.MALE} (man) or ${Gender.FEMALE} (woman)"
    )
    @Column(name = "gender", nullable = false)
    val gender: Int,
) {
    object Gender {
        const val MALE = 1
        const val FEMALE = 2
    }
}