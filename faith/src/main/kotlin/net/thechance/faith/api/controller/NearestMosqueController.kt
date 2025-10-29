package net.thechance.faith.api.controller

import jakarta.validation.Valid
import net.thechance.faith.api.dto.nearestMosque.MosqueRequest
import net.thechance.faith.api.dto.nearestMosque.MosqueResponse
import net.thechance.faith.api.dto.nearestMosque.toMosque
import net.thechance.faith.api.dto.nearestMosque.toMosqueResponse
import net.thechance.faith.service.NearestMosqueService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("faith/mosques")
class NearestMosqueController(
    private val service: NearestMosqueService
) {

    @PostMapping("/create")
    fun createNearestMosque(
        @Valid @RequestBody requestBody: MosqueRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<MosqueResponse> {
        val mosque = service.createNearestMosque(requestBody.toMosque())
        val response = mosque.toMosqueResponse()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/image")
    fun uploadMosqueImage(
        @AuthenticationPrincipal ownerId: UUID,
        @RequestParam("file") file: List<MultipartFile>,
    ): ResponseEntity<List<String>> {
        val imageUrl = service.uploadMosqueImage(ownerId, file)
        return ResponseEntity.ok(imageUrl)
    }

}
