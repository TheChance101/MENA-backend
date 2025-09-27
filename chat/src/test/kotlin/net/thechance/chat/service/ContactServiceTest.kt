package net.thechance.chat.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.chat.entity.Contact
import net.thechance.chat.repository.ContactRepository
import net.thechance.chat.service.model.ContactModel
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.UUID

class ContactServiceTest {

    private val contactRepository: ContactRepository = mockk(relaxed = true)
    private val contactService = ContactService(contactRepository)

    @Test
    fun `getPagedContactByUserId should return paged contacts when called with valid user id and pageable`() {
        val userId = UUID.randomUUID()
        val pageable: Pageable = PageRequest.of(1, 10)
        val contactsModels = listOf(ContactModel(UUID.randomUUID(), "John", "Doe", "123456789", true, UUID.randomUUID(), null))
        val page = PageImpl(contactsModels, pageable, contactsModels.size.toLong())
        every { contactRepository.findAllContactModelsByContactOwnerId(userId, any()) } returns page

        val result = contactService.getPagedContactByUserId(userId, pageable)
        assertThat(result.content).hasSize(contactsModels.size)
        assertThat(result.content.first().firstName).isEqualTo("John")
        verify {
            contactRepository.findAllContactModelsByContactOwnerId(
                userId,
                match { it.pageNumber == 0 && it.pageSize == 10 }
            )
        }
    }

    @Test
    fun `getPagedContactByUserId should return all contacts when pageable pageNumber is less than or equal to zero`() {
        val userId = UUID.randomUUID()
        val pageable: Pageable = PageRequest.of(0, 5)
        val contactsModels = listOf(ContactModel(UUID.randomUUID(), "Jane", "Smith", "987654321", true, UUID.randomUUID(), null))
        val page = PageImpl(contactsModels)
        every { contactRepository.findAllContactModelsByContactOwnerId(userId, any()) } returns page

        val result = contactService.getPagedContactByUserId(userId, pageable)
        assertThat(result.content).hasSize(contactsModels.size)
        assertThat(result.content.first().firstName).isEqualTo("Jane")
        verify {
            contactRepository.findAllContactModelsByContactOwnerId(
                userId,
                match { it.isUnpaged }
            )
        }
    }

    @Test
    fun `syncContacts should save unique contacts and update existing ones`() {
        val userId = UUID.randomUUID()
        val contact1 = Contact(UUID.randomUUID(), "John", "Doe", "123456789", userId)
        val contact2 = Contact(UUID.randomUUID(), "Jane", "Smith", "987654321", userId)
        val contact3 = Contact(UUID.randomUUID(), "John", "Doe", "123456789", userId) // duplicate phone

        every { contactRepository.bulkUpsert(any(), any(), any(), any()) } returns Unit

        contactService.syncContacts(userId, listOf(contact1, contact2, contact3))

        verify(exactly = 1) {
            contactRepository.bulkUpsert(
                userId,
                match { it.toList().containsAll(listOf("123456789", "987654321")) },
                match { it.toList().containsAll(listOf("John", "Jane")) },
                match { it.toList().containsAll(listOf("Doe", "Smith")) }
            )
        }
    }

    @Test
    fun `syncContacts should handle empty contact list`() {
        val userId = UUID.randomUUID()
        contactService.syncContacts(userId, emptyList())
        verify(exactly = 0) { contactRepository.bulkUpsert(any(), any(), any(), any()) }
    }

    @Test
    fun `getPagedContactByUserId should fetch all contacts unpaged when pageNumber or pageSize is zero`() {
        val userId = UUID.randomUUID()
        val pageable: Pageable = PageRequest.of(0, 5)
        val contactsModels = listOf(ContactModel(UUID.randomUUID(), "Alice", "Wonder", "555", true, UUID.randomUUID(), null))
        val page = PageImpl(contactsModels)
        every { contactRepository.findAllContactModelsByContactOwnerId(userId, any()) } returns page

        val result = contactService.getPagedContactByUserId(userId, pageable)

        assertThat(result.content).hasSize(contactsModels.size)
        assertThat(result.content.first().firstName).isEqualTo("Alice")
        verify {
            contactRepository.findAllContactModelsByContactOwnerId(
                userId,
                match { it.isUnpaged }
            )
        }
    }
}
