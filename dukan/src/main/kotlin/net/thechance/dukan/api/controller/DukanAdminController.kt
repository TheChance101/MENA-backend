package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.dukan.DeactivateDukanRequest
import net.thechance.dukan.api.dto.dukan.DukanAdminResponse
import net.thechance.dukan.api.dto.dukan.UpdateDukanStatusRequest
import net.thechance.dukan.api.dto.product.DukanProductAdminResponse
import net.thechance.dukan.api.dto.shelf.DukanShelfResponse
import net.thechance.dukan.api.mapper.dukan.toAdminResponse
import net.thechance.dukan.api.mapper.product.toAdminResponse
import net.thechance.dukan.api.mapper.shelf.toResponse
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.service.DukanProductService
import net.thechance.dukan.service.DukanService
import net.thechance.dukan.service.DukanShelfService
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("$DUKAN_PATH/admin")
class DukanAdminController(
    private val dukanService: DukanService,
    private val dukanShelfService: DukanShelfService,
    private val dukanProductService: DukanProductService
) {

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

    @GetMapping("/shelf/{shelfId}/products")
    fun getShelfProductsForAdmin(
        @PathVariable("shelfId") shelfId: UUID,
        @PageableDefault(size = 10, page = 0, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<DukanProductAdminResponse>> {
        val productsPage: Page<DukanProduct> = dukanProductService.getProductsByShelf(shelfId, pageable)
        val response = productsPage.map { product -> product.toAdminResponse() }
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getDukansByStatusAndSearchQuery(
        @RequestParam(required = false) query: String = "",
        @RequestParam status: Dukan.Status,
        pageable: Pageable
    ): ResponseEntity<Page<DukanAdminResponse>> {
        val dukans = dukanService.getDukansByStatusAndQuery(
            query = query,
            status = status,
            pageable = pageable
        )
        val language = LocaleContextHolder.getLocale().language

        val response = dukans.map { it.toAdminResponse(language) }
        return ResponseEntity.ok(response)
    }

    @PatchMapping("{dukanId}/status")
    fun updateDukanStatus(
        @PathVariable("dukanId") dukanId: UUID,
        @RequestBody updateStatusRequest: UpdateDukanStatusRequest
    ): ResponseEntity<Unit> {
        dukanService.updateDukanStatus(
            dukanId = dukanId,
            status = updateStatusRequest.status,
            reason = updateStatusRequest.reason
        )

        return ResponseEntity.ok().build()
    }

    @PatchMapping("{dukanId}/deactivate")
    fun deactivate(
        @PathVariable("dukanId") dukanId: UUID,
        @RequestBody request: DeactivateDukanRequest
    ): ResponseEntity<String> {
        dukanService.deactivateDukan(dukanId, request.reason)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("{dukanId}/activate")
    fun activate(
        @PathVariable("dukanId") dukanId: UUID
    ): ResponseEntity<String> {
        dukanService.activateDukan(dukanId)
        return ResponseEntity.ok().build()
    }
}
