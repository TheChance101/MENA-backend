package net.thechance.dukan.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DukanController {

    @GetMapping("/dukan/hello")
    fun hello(): String {
        return "Hello Dukan World!"
    }
}