package net.thechance.identity.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
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
	val imageUrl: String?
)