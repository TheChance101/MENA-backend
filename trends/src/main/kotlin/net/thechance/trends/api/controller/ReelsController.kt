package net.thechance.trends.api.controller

import net.thechance.trends.service.ReelsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/reels")
class ReelsController(
    private val reelsService: ReelsService
) {

    @DeleteMapping("/{id}")
    fun deleteReelById(@PathVariable id: UUID): ResponseEntity<Any> {
        return runCatching {
            reelsService.deleteReelById(id)
            ResponseEntity.noContent().build<Any>()
        }.getOrElse { ex ->
            when (ex) {
                is NoSuchElementException ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to ex.message))

                is IllegalAccessException ->
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to ex.message))

                else ->
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(mapOf("error" to "Unexpected error: ${ex.message}"))
            }
        }
    }

}