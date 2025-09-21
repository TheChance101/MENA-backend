package net.thechance.chat.api.controller

import jakarta.validation.Valid
import net.thechance.chat.api.dto.*
import net.thechance.chat.service.ContactService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/chat/contacts")
class ContactController(
    private val contactService: ContactService
) {
    @GetMapping
    fun getPagedContact(
        pageable: Pageable,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<PagedResponse<ContactResponse>> {
        val userId = UUID.fromString(user.username)
        val page = contactService.getPagedContactByUserId(userId, pageable)
        return ResponseEntity.ok(page.toResponse())
    }

    @PostMapping("/sync")
    fun syncContacts(
        @RequestBody @Valid contacts: List<ContactRequest>,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<String> {
        val userId = UUID.fromString(user.username)
        contactService.syncContacts(userId, contacts.toContacts(userId))
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}