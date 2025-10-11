package net.thechance.chat.api.controller

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.chat.entity.ContactUser
import net.thechance.chat.service.ContactUserService
import net.thechance.chat.api.dto.UserDto
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.Test

class UserControllerTest {

    private val contactUserService: ContactUserService = mockk()
    private val controller by lazy { UserController(contactUserService) }

    @Test
    fun `should get user information when getUserById succeeded`(){
        val id = UUID.fromString(userId)
        every { contactUserService.getUserById(id) } returns contactUser

        assertThat(controller.getUserById(id).body).isEqualTo(user)

    }

    @Test
    fun `should throw exception when getUserById throws exception`() {
        val id = UUID.fromString(invalidUserId)
        every { contactUserService.getUserById(id) } throws IllegalArgumentException("User not found")

        assertThrows<IllegalArgumentException> {
            controller.getUserById(id)
        }
    }


    companion object{
        val userId = "451e4d6c-0380-41ed-95e6-275793c404c6"
        val invalidUserId = "451e4d6c-0380-41ed-95e6-275793c404c8"
        val user = UserDto(
            firstName = "omer",
            lastName = "faris",
            phoneNumber = "+9647710222244",
            imageUrl = null
        )
        val contactUser= ContactUser(
            id = UUID.fromString(userId),
            firstName = "omer",
            lastName = "faris",
            phoneNumber = "+9647710222244",
            imageUrl = null
        )
    }
}