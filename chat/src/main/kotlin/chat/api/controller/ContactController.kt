package net.thechance.chat.api.controller

import chat.api.dto.ContactResponse
import chat.api.dto.PagedResponse
import chat.api.dto.toResponse
import chat.service.toResponse
import net.thechance.chat.service.ContactService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contacts")
class ContactController(
    private val contactService: ContactService
) {

    @GetMapping
    fun getPagedContact(pageable: Pageable): ResponseEntity<PagedResponse<ContactResponse>> {
        val page = contactService.getPagedContact(pageable).map { it.toResponse() }
        return ResponseEntity.ok(page.toResponse())
    }
}