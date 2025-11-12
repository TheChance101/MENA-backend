package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.nearestMosque.MosqueRequest
import net.thechance.faith.api.dto.nearestMosque.MosqueResponse
import net.thechance.faith.api.dto.nearestMosque.toMosqueResponse
import net.thechance.faith.service.mosque.FaithImageStorageService
import net.thechance.faith.service.mosque.MosqueService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("faith/mosques")
class NearbyMosqueController(
    private val mosqueService: MosqueService,
    private val imageStorageService: FaithImageStorageService
) {

    @GetMapping("/search")
    fun searchMosquesByName(
        @RequestParam keyword: String,
        @PageableDefault(size = 10, page = 0, sort = ["name"], direction = Direction.ASC)
        pageable: Pageable
    ): ResponseEntity<Page<MosqueResponse>> {
        val mosquesPage = mosqueService.searchMosquesByName(keyword.trim(), pageable)
        val response = mosquesPage.map { it.toMosqueResponse() }

        return ResponseEntity.ok(response)
    }

    @GetMapping("/nearby")
    fun getNearbyMosques(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(defaultValue = "10.0") radiusKm: Double
    ): ResponseEntity<List<MosqueResponse>> {
        val mosques = mosqueService.findNearby(latitude, longitude, radiusKm)
        val response = mosques.map { it.toMosqueResponse() }

        return ResponseEntity.ok(response)
    }

    @PostMapping(consumes = ["multipart/form-data"])
    fun createMosque(
        @RequestPart("mosque") mosqueRequest: MosqueRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<MosqueResponse> {
        val mosque = mosqueService.createNearestMosque(userId = userId, mosqueRequest = mosqueRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(mosque)
    }

    @PutMapping("/{id}/image", consumes = ["multipart/form-data"])
    fun updateMosqueImage(
        @PathVariable id: UUID,
        @RequestPart("image") image: MultipartFile,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<MosqueResponse> {
        val newImageUrl = imageStorageService.uploadImage(
            file = image,
            fileName = "mosque-$id",
            folderName = "mosques"
        )

        val updatedMosque = mosqueService.updateMosqueImage(id, newImageUrl)

        return ResponseEntity.ok(updatedMosque.toMosqueResponse())
    }
}

