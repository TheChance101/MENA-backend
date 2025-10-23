package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import net.thechance.dukan.api.constants.EndPoints.DUKAN_PATH
import net.thechance.dukan.api.dto.product.DukanProductCreationRequest
import net.thechance.dukan.api.dto.product.DukanProductCreationResponse
import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.service.DukanProductService
import net.thechance.dukan.service.mapper.product.toProductCreationParams
import net.thechance.dukan.service.mapper.product.toProductResponse
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
        val productId = dukanProductService.createProduct(request.toProductCreationParams(userId))
        return ResponseEntity.ok(DukanProductCreationResponse(productId))
    }

    @GetMapping("/{shelfId}")
    fun getProductsByShelf(
        @PathVariable shelfId: UUID,
        @PageableDefault(size = 10, page = 0, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<DukanProductResponse>> {
        val products = dukanProductService.getProductsByShelf(shelfId, pageable)
        val productsResponse = products.map { it.toProductResponse() }
        return ResponseEntity.ok(productsResponse)
    }
}