package net.thechance.chat.controller

import chat.dto.PagedResponse
import net.thechance.chat.dto.BaseResponse
import net.thechance.chat.dto.ContactResponse
import net.thechance.chat.dto.baseResponse
import net.thechance.chat.service.ContactService
import net.thechance.identity.security.JwtFilter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contacts")
class ContactController(
    private val contactService: ContactService,
) {

    @GetMapping
    fun getPagedContact(
        @RequestParam pageNumber: Int = 1,
        @RequestParam pageSize: Int = 20,
    ): ResponseEntity<BaseResponse<PagedResponse<ContactResponse>>> {
        val userId = JwtFilter.getUserId()

        val data = contactService.getPagedContacts(userId = userId, pageNumber = pageNumber, pageSize = pageSize)
        return baseResponse(
            data = data,
            message = "fetch data successfully",
            success = true,
        )
    }
}