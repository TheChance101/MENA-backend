package net.thechance.identity

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IdentityController {

    @GetMapping("/identity/hello")
    fun identityHello(): String {
        return "Hello identity World!"
    }
}