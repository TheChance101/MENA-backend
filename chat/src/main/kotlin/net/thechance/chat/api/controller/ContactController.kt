package net.thechance.chat.api.controller

import jakarta.validation.Valid
import net.thechance.chat.api.dto.*
import net.thechance.chat.service.ContactService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/chat/contacts")
class ContactController(
    private val contactService: ContactService
) {
    @GetMapping
    fun getPagedContact(
        pageable: Pageable,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<PagedResponse<ContactResponse>> {
        val page = contactService.getPagedContactByUserId(userId, pageable)
        return ResponseEntity.ok(page.toResponse())
    }

    @PostMapping("/sync")
    fun syncContacts(
        @RequestBody @Valid contacts: List<ContactRequest>,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<String> {
        contactService.syncContacts(userId, contacts.toContacts(userId))
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}