package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.DukanProductResponse
import net.thechance.dukan.api.dto.toProductResponse
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.service.DukanProductService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/products")
class DukanProductController(
    private val productService: DukanProductService
) {
    @GetMapping("/shelf/{shelfId}")
    fun getProductsByShelf(
        @PathVariable shelfId: UUID,
        @PageableDefault(size = 10, page = 0,sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<DukanProductResponse>> {
        val products = productService.getProductsByShelf(shelfId,pageable)
        val productsResponse = products.map { it.toProductResponse() }
        return ResponseEntity.ok(productsResponse)
    }
}