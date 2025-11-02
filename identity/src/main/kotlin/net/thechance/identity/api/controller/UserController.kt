package net.thechance.identity.api.controller

import net.thechance.identity.api.dto.ManagedUserResponse
import net.thechance.identity.api.dto.PageResponse
import net.thechance.identity.api.dto.UpdateUserStatusRequest
import net.thechance.identity.api.mapper.toUserResponsePage
import net.thechance.identity.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController("identityUserController")
@RequestMapping("/identity/admin/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun getUsers(
        @RequestParam(required = false) query: String = "",
        pageable: Pageable
    ): ResponseEntity<PageResponse<ManagedUserResponse>> {

        val response = userService.findUsersByQuery(
            query = query,
            pageable = pageable
        ).toUserResponsePage()

        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{userId}/status")
    fun updateUserStatus(
        @PathVariable userId: UUID,
        @RequestBody updateStatusRequest: UpdateUserStatusRequest
    ): ResponseEntity<Unit> {

        userService.updateUserStatus(
            userId = userId,
            status = updateStatusRequest.status
        )

        return ResponseEntity.ok().build()
    }
}