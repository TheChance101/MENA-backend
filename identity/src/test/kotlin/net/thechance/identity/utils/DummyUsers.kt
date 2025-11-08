package net.thechance.identity.utils

import net.thechance.identity.entity.User
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

object DummyUsers {
	val validUser1 = User(
        phoneNumber = "+201293393331",
        password = "test12300",
        firstName = "test",
        lastName = "",
        username = "",
        imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
    )
	val validUser2 = User(
		phoneNumber = "+201293393332",
		password = "test12301",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
	val validUser3 = User(
		phoneNumber = "+201293393333",
		password = "test12302",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
	val validUser4 = User(
		phoneNumber = "+201293393334",
		password = "test12303",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
	val validUser5 = User(
		phoneNumber = "+201293393335",
		password = "test12304",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
	val validUser6 = User(
		phoneNumber = "+201293393336",
		password = "test12305",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)

    val blockedUser = User(
        phoneNumber = "+201293393331", password = "Test1234", firstName = "",
        lastName = "",
        username = "",
        imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.BLOCKED
    )

    val userWithInvalidPasswordLength = User(
		phoneNumber = "+201293393331", password = "test", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
	val userWithInvalidPassword = User(
		phoneNumber = "+201293393331", password = "test1234453", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
	val userWithInvalidPhoneNumber = User(
		phoneNumber = "123456789", password = "test12300", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
	val userWithInvalidPasswordAndPhoneNumber = User(
		phoneNumber = "123456789", password = "test123456", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null,
        id = UUID.randomUUID(),
        birthDate = LocalDate.now(),
        gender = User.Gender.MALE.toInt(),
        lastVisitAt = LocalDateTime.now(),
        lastLoginAt = LocalDateTime.now(),
        status = User.Status.ACTIVE
	)
}