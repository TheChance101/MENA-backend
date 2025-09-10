package net.thechance.chat.controller

import chat.dto.PagedResponse
import net.thechance.chat.entity.Contact
import net.thechance.chat.service.ContactService
import net.thechance.mena.mapper.toPagedResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contact")
class ContactController(private val contactService: ContactService) {

    @GetMapping
    fun getContacts(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): PagedResponse<Contact> {
        val pageResult = contactService.getAllContacts(pageNumber = pageNumber, pageSize = pageSize)
        return pageResult.toPagedResponse()
    }
}