package net.thechance.wallet

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WalletController {

    @GetMapping("/wallet/hello")
    fun hello(): String {
        return "Hello wallet World!"
    }
}