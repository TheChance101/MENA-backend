package net.thechance.faith.api.controller

import net.thechance.faith.entity.Mosque
import net.thechance.faith.service.mosque.FaithImageStorageService
import net.thechance.faith.service.mosque.MosqueService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("faith/mosques")
class MosqueController(
    private val mosqueService: MosqueService,
    private val imageStorageService: FaithImageStorageService
) {


    @PostMapping(consumes = ["multipart/form-data"])
    fun createMosque(
        @RequestParam("name") name: String,
        @RequestParam("address") address: String,
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double,
        @RequestPart("image") image: MultipartFile
    ): ResponseEntity<Any> {
        return try {
            val imageUrl = imageStorageService.uploadImage(
                file = image,
                fileName = name,
                folderName = "mosques"
            )

            val mosque = Mosque(
                name = name,
                latitude = latitude,
                longitude = longitude,
                imageUrl = imageUrl,
                address = address,
                id = UUID.randomUUID(),
                createdAt = Instant.now()
            )
            mosqueService.createNearestMosque(mosque)

            ResponseEntity.ok(mapOf("success" to true, "mosque" to mosque))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("success" to false, "error" to e.message))
        }
    }


    @PutMapping("/{id}/image", consumes = ["multipart/form-data"])
    fun updateMosqueImage(
        @PathVariable id: UUID,
        @RequestPart("image") image: MultipartFile
    ): ResponseEntity<Any> {
        val mosque = mosqueService.getMosqueById(id)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("success" to false, "error" to "Mosque not found"))


        val newImageUrl = imageStorageService.uploadImage(
            file = image,
            fileName = mosque.name,
            folderName = "mosques"
        )

        mosqueService.updateMosqueImage(id, newImageUrl)

        return ResponseEntity.ok(mapOf("success" to true, "mosque" to mosque.copy(imageUrl = newImageUrl)))
    }
}

