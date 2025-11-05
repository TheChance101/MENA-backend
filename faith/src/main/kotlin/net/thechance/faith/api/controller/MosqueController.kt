package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.nearestMosque.MosqueRequest
import net.thechance.faith.api.dto.nearestMosque.MosqueResponse
import net.thechance.faith.api.dto.nearestMosque.toMosque
import net.thechance.faith.api.dto.nearestMosque.toMosqueResponse
import net.thechance.faith.service.mosque.FaithImageStorageService
import net.thechance.faith.service.mosque.MosqueService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("faith/mosques")
class MosqueController(
    private val mosqueService: MosqueService,
    private val imageStorageService: FaithImageStorageService
) {


    @PostMapping(consumes = ["multipart/form-data"])
    fun createMosque(
        @RequestPart("mosque") mosqueRequest: MosqueRequest,
        @RequestPart("image") image: MultipartFile
    ): ResponseEntity<Map<String, MosqueResponse>> {
        val imageUrl = imageStorageService.uploadImage(
            file = image,
            fileName = mosqueRequest.name,
            folderName = "mosques"
        )

        val mosque = mosqueService.createNearestMosque(
            mosqueRequest.toMosque(imageUrl)
        )

        val response = mapOf(
            "mosque" to mosque.toMosqueResponse()
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }


    @PutMapping("/{id}/image", consumes = ["multipart/form-data"])
    fun updateMosqueImage(
        @PathVariable id: UUID,
        @RequestPart("image") image: MultipartFile
    ): ResponseEntity<Map<String, MosqueResponse>> {
        val newImageUrl = imageStorageService.uploadImage(
            file = image,
            fileName = "mosque-$id",
            folderName = "mosques"
        )

        val updatedMosque = mosqueService.updateMosqueImage(id, newImageUrl)

        return ResponseEntity.ok(
            mapOf(
                "mosque" to updatedMosque.toMosqueResponse()
            )
        )
    }
}

