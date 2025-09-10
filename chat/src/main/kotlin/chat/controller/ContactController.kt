package net.thechance.chat.controller

import net.thechance.chat.service.ContactService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contact")
class ContactController (private val contactService : ContactService) {

    @GetMapping("/hello")
    fun hello(): String{
        return "hello"
    }
}