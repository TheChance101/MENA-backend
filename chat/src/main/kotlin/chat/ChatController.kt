package net.thechance.chat

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ChatController {

    @GetMapping("/chat/hello")
    fun chatHello(): String {
        return "Hello chat World!"
    }
}

