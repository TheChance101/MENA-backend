package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.nearestMosque.MosqueResponse
import net.thechance.faith.api.dto.nearestMosque.toMosqueResponse
import net.thechance.faith.service.MosqueService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("faith/mosques")
class NearbyMosqueController(
    private val mosqueService: MosqueService
) {

    @GetMapping("/search")
    fun searchMosquesByName(
        @RequestParam keyword: String,
        @PageableDefault(size = 10, page = 0, sort = ["name"], direction = Direction.ASC)
        pageable: Pageable
    ): ResponseEntity<Page<MosqueResponse>> {
        val mosquesPage = mosqueService.searchMosquesByName(keyword.trim(), pageable)
        val response = mosquesPage.map { it.toMosqueResponse() }

        return ResponseEntity.ok(response)
    }
}
