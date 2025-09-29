package net.thechance.chat.service

import net.thechance.chat.repository.ContactUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ContactUserService(
    private val contactUserRepository: ContactUserRepository
) {
    fun getPhoneNumberByIdOrNull(id: UUID) = contactUserRepository.findByIdOrNull(id)?.phoneNumber
}