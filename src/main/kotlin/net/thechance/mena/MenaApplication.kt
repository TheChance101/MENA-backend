package net.thechance.mena

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class MenaApplication

fun main(args: Array<String>) {
    runApplication<MenaApplication>(*args)
}

@RestController
class HelloController {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello MENA World!"
    }
}
