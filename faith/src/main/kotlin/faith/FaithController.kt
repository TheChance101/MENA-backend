package net.thechance.faith

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FaithController {

    @GetMapping("/faith/hello")
    fun hello(): String {
        return "hello faith world!"
    }
}