package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.dto.product.*
import net.thechance.dukan.api.mapper.product.toProductCreationParams
import net.thechance.dukan.api.mapper.product.toProductUpdateParams
import net.thechance.dukan.api.mapper.product.toResponse
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.DukanProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("${DUKAN_PATH}/product")
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
        val productId = dukanProductService.createProduct(
            request.toProductCreationParams(userId)
        )
        return ResponseEntity.ok(DukanProductCreationResponse(productId))
    }

    @GetMapping
    fun getProductsByShelf(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam shelfId: UUID,
        @PageableDefault(size = 10, page = 0, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<DukanProductResponse>> {
        val products = dukanProductService.getProductsByShelf(userId, shelfId, pageable)

        val productsResponse = products.map { it.toResponse() }

        return ResponseEntity.ok(productsResponse)
    }

    @GetMapping("/{productId}")
    fun getProductById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable("productId") productId: UUID
    ): ResponseEntity<DukanProductResponse> {
        val product = dukanProductService.getProductById(userId, productId)

        return ResponseEntity.ok(product.toResponse())
    }

    @PutMapping("/{productId}")
    fun updateProduct(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable("productId") productId: UUID,
        @Valid @RequestBody request: DukanProductUpdateRequest,
    ): DukanProductUpdateResponse {
        val productUpdateParams = request.toProductUpdateParams(userId, productId)
        val productId = dukanProductService.updateProduct(productUpdateParams)

        return DukanProductUpdateResponse(productId)
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable productId: UUID
    ): ResponseEntity<Unit> {
        dukanProductService.deleteProduct(userId, productId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{productId}/image")
    fun uploadProductImage(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable productId: UUID,
        @RequestParam("file") file: MultipartFile,
    ): String {
        val imageUrl = dukanProductService.uploadProductImage(userId, productId, file)
        return imageUrl
    }

    @PostMapping("{productId}/favorite")
    fun toggleFavoriteStatus(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable productId: UUID,
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(dukanProductService.toggleFavoriteStatus(userId, productId))
    }

    @GetMapping("{dukanId}/best_selling")
    fun getBestSelling(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable dukanId: UUID,
        @PageableDefault(size = 10, page = 0)
        pageable: Pageable
    ): ResponseEntity<Page<DukanProductResponse>> {
        val products = dukanProductService.getBestSellingProducts(userId, dukanId, pageable)
        val productsResponse = products.map { it.toResponse() }

        return ResponseEntity.ok(productsResponse)
    }
}