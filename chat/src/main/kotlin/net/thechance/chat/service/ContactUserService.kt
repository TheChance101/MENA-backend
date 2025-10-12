package net.thechance.chat.service

import net.thechance.chat.repository.ContactUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContactUserService(
    private val contactUserRepository: ContactUserRepository
) {
    fun getPhoneNumberByUserId(id: UUID) = getUserById(id).phoneNumber
    fun getUserById(id: UUID) = contactUserRepository.findByIdOrNull(id) ?: throw IllegalArgumentException("User not found")
}