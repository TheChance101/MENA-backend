package net.thechance.trends

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TrendsController {

    @GetMapping("/trends/hello")
    fun hello(): String {
        return "Hello trends World!"
    }
}