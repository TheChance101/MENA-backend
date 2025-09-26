package net.thechance.identity.service

import net.thechance.identity.api.dto.ProfileResponse
import net.thechance.identity.entity.User
import net.thechance.identity.exception.InvalidCredentialsException
import net.thechance.identity.exception.UserNotFoundException
import net.thechance.identity.mapper.toResponse
import net.thechance.identity.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
	val userRepository: UserRepository
) {

	fun findByPhoneNumber(phoneNumber: String): User {
		return userRepository.findByPhoneNumber(phoneNumber) ?: throw InvalidCredentialsException("User not found")
	}

	fun findById(userId: UUID): ProfileResponse {
		return userRepository.findByIdOrNull(userId)?.toResponse()
			?: throw UserNotFoundException("User with id: $userId not found")
	}

	fun userExists(userId: UUID): Boolean {
		return userRepository.existsById(userId)
	}
}