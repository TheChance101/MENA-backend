package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.DukanProductResponse
import net.thechance.dukan.api.dto.toProductResponse
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.service.DukanProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
import java.util.*

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