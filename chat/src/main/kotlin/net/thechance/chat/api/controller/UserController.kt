package net.thechance.chat.api.controller

import net.thechance.chat.service.ContactUserService
import net.thechance.chat.api.dto.UserDto
import net.thechance.chat.api.dto.toDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/chat/user")
class UserController (
    private val contactUserService: ContactUserService
){

    @GetMapping
    fun getUserById(
        @RequestParam id: String
    ):  ResponseEntity<UserDto>{
        val userId = UUID.fromString(id)
        return ResponseEntity.ok(contactUserService.getUserById(userId).toDto())
    }
}