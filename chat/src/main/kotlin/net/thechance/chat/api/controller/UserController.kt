package net.thechance.chat.api.controller

import net.thechance.chat.api.dto.UserDto
import net.thechance.chat.api.dto.toResponse
import net.thechance.chat.service.ContactUserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/chat/user")
class UserController (
    private val contactUserService: ContactUserService
){

    @GetMapping
    fun getUserById(
        @AuthenticationPrincipal userId : UUID
    ):  ResponseEntity<UserDto>{
        return ResponseEntity.ok(contactUserService.getUserById(userId).toResponse())
    }
}