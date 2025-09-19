package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.ContactResponse
import net.thechance.chat.api.dto.PagedResponse
import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.service.ContactService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
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
}