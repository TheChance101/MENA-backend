package net.thechance.chat.service

import jakarta.transaction.Transactional
import net.thechance.chat.service.exception.NotFoundException
import net.thechance.chat.repository.ContactUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContactUserService(
    private val contactUserRepository: ContactUserRepository,
) {
    fun getPhoneNumberByUserId(id: UUID) = contactUserRepository.findPhoneNumberById(id)
    fun getUserById(id: UUID) = contactUserRepository.findByIdOrNull(id) ?: throw NotFoundException("User not found with this id $id")

    @Transactional
    fun deleteUser(userId: UUID) {
        val updated = contactUserRepository.updateIsDeleted(userId, isDeleted = true)
        if (updated == 0) throw NotFoundException("User with id $userId not found")
    }
}