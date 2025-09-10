package chat.controller

import chat.service.ContactService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contact")
class ContactController (private val contactService : ContactService) {

}