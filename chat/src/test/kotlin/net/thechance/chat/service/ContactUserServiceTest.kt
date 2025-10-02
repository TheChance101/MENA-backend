package net.thechance.chat.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.repository.ContactUserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class ContactUserServiceTest {

    private val contactUserRepository: ContactUserRepository = mockk(relaxed = true)
    private val service = ContactUserService(contactUserRepository)

    @Test
    fun `getPhoneNumberByIdOrNull returns phone number when user exists`() {
        val userId = UUID.randomUUID()
        val user = ContactUser(
            id = userId,
            firstName = "Raouf",
            lastName = "Kamel",
            phoneNumber = "+967775074564",
            imageUrl = null
        )

        every { contactUserRepository.findByIdOrNull(userId) } returns user

        val result = service.getPhoneNumberByUserId(userId)

        assertThat(result).isEqualTo("+967775074564")
    }

    @Test
    fun `getPhoneNumberByIdOrNull throws IllegalArgumentException when user does not exist`() {
        val userId = UUID.randomUUID()

        every { contactUserRepository.findByIdOrNull(userId) } returns null

        val exception = assertThrows<IllegalArgumentException> { service.getPhoneNumberByUserId(userId) }
        assertThat(exception).hasMessageThat().contains("User not found")
    }
}
