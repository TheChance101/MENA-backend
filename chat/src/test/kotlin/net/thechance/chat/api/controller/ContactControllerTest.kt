package net.thechance.chat.api.controller

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import net.thechance.chat.api.dto.ContactRequest
import net.thechance.chat.service.ContactService
import net.thechance.chat.service.model.ContactModel
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import com.google.common.truth.Truth.assertThat
import java.util.UUID

class ContactControllerTest {

    private val contactService: ContactService = mockk()

    private val controller by lazy { ContactController(contactService) }

    @Test
    fun `getPagedContact should return paged contacts when called with valid user id and pageable`() {
        val userId = UUID.randomUUID()
        val pageable: Pageable = PageRequest.of(0, 10)
        val contactsModels = listOf(ContactModel(UUID.randomUUID(), "John", "Doe", "123456789", UUID.randomUUID(), null))
        val page = PageImpl(contactsModels, pageable, contactsModels.size.toLong())
        every { contactService.getPagedContactByUserId(any(), any()) } returns page

        val response = controller.getPagedContact(pageable, userId)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `syncContacts should return created status when called with valid contacts and user id`() {
        val userId = UUID.randomUUID()
        val contacts = listOf(ContactRequest("John", "Doe", "123456789"))
        justRun { contactService.syncContacts(any(), any()) }

        val response = controller.syncContacts(contacts, userId)
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }
}
