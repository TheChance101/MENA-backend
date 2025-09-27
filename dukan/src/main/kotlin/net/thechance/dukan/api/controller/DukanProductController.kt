package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.dto.DukanProductCreateResponse
import net.thechance.dukan.api.dto.DukanProductCreationRequest
import net.thechance.dukan.mapper.toProductCreationParams
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

    @PostMapping("/create")
    fun createProduct(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody request: DukanProductCreationRequest,
    ): ResponseEntity<DukanProductCreateResponse> {
        val productId = dukanProductService.createProduct(request.toProductCreationParams(userId))
        return ResponseEntity.ok(DukanProductCreateResponse(productId))
    }
}