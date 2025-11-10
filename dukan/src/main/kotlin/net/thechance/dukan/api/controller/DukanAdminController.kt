package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.dukan.DukanAdminDetailsResponse
import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.api.dto.shelf.DukanShelfResponse
import net.thechance.dukan.api.mapper.category.DukanLanguage
import net.thechance.dukan.api.mapper.category.toDto
import net.thechance.dukan.api.mapper.product.toAdminResponse
import net.thechance.dukan.api.mapper.dukan.toResponse
import net.thechance.dukan.api.mapper.shelf.toResponse
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.service.DukanProductService
import net.thechance.dukan.service.DukanService
import net.thechance.dukan.service.DukanShelfService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("$DUKAN_PATH/admin")
class DukanAdminController(
    private val dukanService: DukanService,
    private val dukanShelfService: DukanShelfService,
    private val dukanProductService: DukanProductService
) {

    @GetMapping("/{dukanId}")
    fun getDukanDetails(@PathVariable("dukanId") dukanId: UUID): ResponseEntity<DukanAdminDetailsResponse> {
        val dukan = dukanService.getDukanDetailsById(dukanId)
        val dukanDetails = dukan.toResponse()

        // TODO replace default Arabic with the extracted language from the header
        val categories = dukan.categories.map { category -> category.toDto(DukanLanguage.ARABIC) }

        val response = DukanAdminDetailsResponse(
            dukan = dukanDetails,
            categories = categories,
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/shelf/{dukanId}")
    fun getAllShelvesByDukanId(
        @PathVariable dukanId: UUID,
        @PageableDefault(size = 10, page = 0)
        pageable: Pageable
    ): ResponseEntity<Page<DukanShelfResponse>> {
        val shelvesPage = dukanShelfService.getAllShelvesByDukanId(dukanId, pageable)
            .map(DukanShelf::toResponse)
        return ResponseEntity.ok(shelvesPage)
    }

    @GetMapping("/shelves/{shelfId}/products")
    fun getShelfProductsForAdmin(
        @PathVariable("shelfId") shelfId: UUID,
        @PageableDefault(size = 10, page = 0, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<DukanProductResponse>> {
        val productsPage: Page<DukanProduct> = dukanProductService.getProductsByShelf(shelfId, pageable)
        val response = productsPage.map { product -> product.toAdminResponse() }
        return ResponseEntity.ok(response)
    }
}
