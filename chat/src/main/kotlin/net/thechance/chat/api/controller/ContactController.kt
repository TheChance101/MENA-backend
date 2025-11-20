package net.thechance.chat.api.controller

import jakarta.validation.Valid
import net.thechance.chat.api.dto.*
import net.thechance.chat.service.ContactService
import net.thechance.chat.service.model.SearchContactsArgs
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
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
        contactService.syncContacts(contacts.toContacts(userId), userId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/search")
    fun searchContacts(
        @RequestParam query: String,
        @RequestParam(defaultValue = "false") onlyMenaUsers: Boolean,
        pageable: Pageable,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<PagedResponse<ContactResponse>> {
        val args = SearchContactsArgs(query, userId, onlyMenaUsers, pageable)
        val results = contactService.searchContacts(args)
        return ResponseEntity.ok(results.toResponse())
    }

}