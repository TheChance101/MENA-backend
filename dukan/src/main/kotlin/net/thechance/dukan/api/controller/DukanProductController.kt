package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.dto.DukanProductCreationResponse
import net.thechance.dukan.api.dto.DukanProductCreationRequest
import net.thechance.dukan.mapper.toProductCreationParams
import net.thechance.dukan.api.dto.DukanProductResponse
import net.thechance.dukan.api.dto.toProductResponse
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

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
    ): ResponseEntity<DukanProductCreationResponse> {
        val productId = dukanProductService.createProduct(request.toProductCreationParams(userId))
        return ResponseEntity.ok(DukanProductCreationResponse(productId))
    }

    @GetMapping("/{shelfId}")
    fun getProductsByShelf(
        @PathVariable shelfId: UUID,
        @PageableDefault(size = 10, page = 0,sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<DukanProductResponse>> {
        val products = dukanProductService.getProductsByShelf(shelfId,pageable)
        val productsResponse = products.map { it.toProductResponse() }
        return ResponseEntity.ok(productsResponse)
    }
}