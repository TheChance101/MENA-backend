package net.thechance.identity.utils

import net.thechance.identity.entity.User

object DummyUsers {
	val validUser1 = User(
		phoneNumber = "+201293393331",
		password = "test12300",
		firstName = "test",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val validUser2 = User(
		phoneNumber = "+201293393332",
		password = "test12301",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val validUser3 = User(
		phoneNumber = "+201293393333",
		password = "test12302",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val validUser4 = User(
		phoneNumber = "+201293393334",
		password = "test12303",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val validUser5 = User(
		phoneNumber = "+201293393335",
		password = "test12304",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val validUser6 = User(
		phoneNumber = "+201293393336",
		password = "test12305",
		firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)

	val userWithInvalidPasswordLength = User(
		phoneNumber = "+201293393331", password = "test", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val userWithInvalidPassword = User(
		phoneNumber = "+201293393331", password = "test1234453", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val userWithInvalidPhoneNumber = User(
		phoneNumber = "123456789", password = "test12300", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
	val userWithInvalidPasswordAndPhoneNumber = User(
		phoneNumber = "123456789", password = "test123456", firstName = "",
		lastName = "",
		username = "",
		imageUrl = null
	)
}