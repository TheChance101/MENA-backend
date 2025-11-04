package net.thechance.dukan.api.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.api.dto.category.DukanCategoryResponse
import net.thechance.dukan.api.dto.color.DukanColorResponse
import net.thechance.dukan.api.dto.dukan.*
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.service.DukanService
import net.thechance.dukan.api.mapper.category.DukanLanguage
import net.thechance.dukan.api.mapper.category.toDto
import net.thechance.dukan.api.mapper.dukan.toDto
import net.thechance.dukan.api.mapper.dukan.toDukanCreationParams
import net.thechance.dukan.api.mapper.dukan.toDukanResponse
import net.thechance.dukan.api.mapper.dukan.toDukanStyleResponse
import net.thechance.dukan.api.mapper.dukan.toResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import kotlin.collections.emptyList


@RestController
@RequestMapping(DUKAN_PATH)
class DukanController(
    private val dukanService: DukanService,
) {
    @GetMapping("/styles")
    fun getAllStyles(): ResponseEntity<DukanStyleResponse> {
        val styles = dukanService.getAllStyles().toDukanStyleResponse()
        return ResponseEntity.ok(styles)
    }

    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<DukanCategoryResponse> {
        val categories = dukanService.getAllCategories().let { categories ->
            // Todo replace default Arabic with the extracted language from the header
            categories.map { category -> category.toDto(DukanLanguage.ARABIC) }
        }
        return ResponseEntity.ok(DukanCategoryResponse(categories))
    }

    @GetMapping("/available")
    fun checkNameAvailability(
        @RequestParam(name = "name") @NotBlank name: String
    ): ResponseEntity<DukanNameAvailabilityResponse> {
        val available = dukanService.isDukanNameAvailable(name)
        return ResponseEntity.ok(DukanNameAvailabilityResponse(available))
    }

    @PostMapping("/create")
    fun createDukan(
        @Valid @RequestBody requestBody: DukanCreationRequest,
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<Dukan> {
        val dukan = dukanService.createDukan(requestBody.toDukanCreationParams(userId))
        return ResponseEntity.ok(dukan)
    }

    @PostMapping("/image")
    fun uploadDukanImage(
        @AuthenticationPrincipal ownerId: UUID,
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<String> {
        val imageUrl = dukanService.uploadDukanImage(ownerId, file)
        return ResponseEntity.ok(imageUrl)
    }

    @GetMapping("/colors")
    fun getAllColors(): ResponseEntity<DukanColorResponse> {
        val colors = dukanService.getAllColors().map { it.toDto() }
        return ResponseEntity.ok(DukanColorResponse(colors))
    }

    @GetMapping("/statues")
    fun getDukanStatues(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<DukanStatuesResponse> {
        val dukan = dukanService.getDukanByOwnerId(userId)
        return ResponseEntity.ok(DukanStatuesResponse(dukan.name, dukan.status))
    }

    @GetMapping("/categories/{categoryId}")
    fun getAllByCategoryId(
        @AuthenticationPrincipal userId: UUID?,
        @PathVariable("categoryId") categoryId: UUID,
        @PageableDefault(size = 10, page = 0, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<DukanResponse>> {

        val dukansPage = dukanService.getAllByCategoryId(categoryId, pageable)
        val response = mapDukansWithFavorites(userId, dukansPage)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/editor_picks")
    fun getEditorPicksDukan(
        @AuthenticationPrincipal userId: UUID?,
        @PageableDefault(size = 5, page = 0, sort = ["createdAt"], direction = Sort.Direction.DESC)
        pageable: Pageable
    ): ResponseEntity<Page<DukanResponse>> {
        val response = getAllEditorPicksWithFavorites(userId, pageable)
        return ResponseEntity.ok(response)
    }

    private fun getAllEditorPicksWithFavorites(userId: UUID?, pageable: Pageable): Page<DukanResponse> {
        val dukansPage = dukanService.getAllEditorPicksDukan(userId, pageable)
        return mapDukansWithFavorites(userId, dukansPage)
    }

    private fun mapDukansWithFavorites(userId: UUID?, dukans: Page<Dukan>): Page<DukanResponse> {
        val favoriteIds = if (userId != null) {
            dukanService.getUserFavorites(userId).map { it.dukan.id }
        } else emptyList()

        return dukans.map { dukan ->
            dukan.toDukanResponse(isFavorite = favoriteIds.contains(dukan.id))
        }
    }

    @GetMapping("/{dukanId}")
    fun getDukanDetailsById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable("dukanId") dukanId: UUID): ResponseEntity<DukanDetailsResponse> {
        val dukan = dukanService.getDukanDetailsById(dukanId)
        val isFavorite = dukanService.isFavorite(userId, dukanId)

        val dukanDetailsResponse = dukan.toResponse(isFavorite)
        return ResponseEntity.ok(dukanDetailsResponse)
    }

    @GetMapping("/nearby/best")
    fun getBestDukansAround(
        @RequestParam lat: Double,
        @RequestParam lng: Double,
        @RequestParam(required = false, defaultValue = "30000") range: Double,
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<DukanResponse>> {
        val dukans = dukanService.getAllBestDukansAround(lat, lng, pageable, range)
        val response = dukans.map { it.toDukanResponse() }
        return ResponseEntity.ok(response)
    }

    @PostMapping("{dukanId}/favorite")
    fun toggleFavoriteStatus(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable dukanId: UUID,
    ): ResponseEntity<Boolean> {
        val response = dukanService.toggleFavoriteStatus(userId, dukanId)
        return ResponseEntity.ok(response)
    }
}