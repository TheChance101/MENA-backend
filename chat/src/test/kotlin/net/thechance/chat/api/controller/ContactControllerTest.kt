package net.thechance.chat.api.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import net.thechance.chat.api.dto.ContactRequest
import net.thechance.chat.service.ContactService
import net.thechance.chat.service.model.ContactModel
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.User
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootApplication
class ChatApplication

@WebMvcTest(ContactController::class)
@ContextConfiguration(classes = [ChatApplication::class])
class ContactControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var contactService: ContactService

    private val objectMapper = jacksonObjectMapper()

    @Test
    @WithMockUser
    fun `getPagedContact should return paged contacts when called with valid user id and pageable`() {
        val userId = UUID.randomUUID()
        val pageable: Pageable = PageRequest.of(0, 10)
        val contactsModels = listOf(ContactModel(UUID.randomUUID(), "John", "Doe", "123456789", true, null))
        val page = PageImpl(contactsModels, pageable, contactsModels.size.toLong())
        every { contactService.getPagedContactByUserId(any(), any()) } returns page
        val userDetails = User(userId.toString(), "password", listOf())
        mockMvc.perform(
            get("/chat/contacts").with(user(userDetails)).param("page", "0").param("size", "10")
        ).andExpect(status().isOk)
    }

    @Test
    @WithMockUser
    fun `syncContacts should return created status when called with valid contacts and user id`() {
        val userId = UUID.randomUUID()
        val contacts = listOf(ContactRequest("John", "Doe", "123456789"))
        justRun { contactService.syncContacts(any(), any()) }
        val userDetails = User(userId.toString(), "password", listOf())

        mockMvc.perform(
            post("/chat/contacts/sync").with(user(userDetails)).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contacts))
        )
    }

    @Test
    fun `getPagedContact should return 401 when no authentication provided`() {
        mockMvc.perform(
            get("/chat/contacts").param("page", "0").param("size", "10")
        ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `syncContacts should return 401 when no authentication provided`() {
        mockMvc.perform(
            post("/chat/contacts/sync").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("[]")
        ).andExpect(status().isUnauthorized)
    }
}
