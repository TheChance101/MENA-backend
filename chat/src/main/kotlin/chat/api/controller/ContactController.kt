package chat.api.controller

import chat.api.dto.ContactRequest
import chat.service.ContactService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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

    @PostMapping("/sync")
    fun syncContacts(
        @RequestBody contacts: List<ContactRequest>,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(
            contactService.syncContacts(userId, contacts)
        )
    }
}