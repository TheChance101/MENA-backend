package net.thechance.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@ComponentScan(basePackages = ["net.thechance.app", "net.thechance.identity", "net.thechance.chat"])
class MenaApplication

fun main(args: Array<String>) {
    runApplication<MenaApplication>(*args)
}

@RestController
class HelloController {

    @GetMapping("/app/hello")
    fun hello(): String {
        return "Hello app World!"
    }
}