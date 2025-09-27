package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.ProductCreationRequest
import net.thechance.dukan.service.DukanProductService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/dukan/product")
class DukanProductController(
    private val dukanProductService: DukanProductService
) {
    @PostMapping("/images/{productId}")
    fun uploadProductImages(
        @PathVariable("productId") productId: UUID,
        @RequestParam("files") files: List<MultipartFile>,
    ): ResponseEntity<List<String>> {
        val imageUrls = dukanProductService.uploadProductImages(
            productId = productId,
            files = files
        )
        return ResponseEntity.ok(imageUrls)
    }

    @PostMapping("/create/{shelfId}")
    fun createProduct(
        @PathVariable shelfId: UUID,
        @AuthenticationPrincipal userId: UUID,
        @RequestBody  request: ProductCreationRequest,
    ): ResponseEntity<UUID> {
        val productId = dukanProductService.createProduct(
            ownerId = userId,
            shelfId = shelfId,
            name = request.name,
            description = request.description,
            price = request.price,
        )
        return ResponseEntity.ok(productId)
    }
}