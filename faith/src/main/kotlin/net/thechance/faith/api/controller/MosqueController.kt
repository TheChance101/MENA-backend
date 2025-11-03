package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.nearestMosque.MosqueResponse
import net.thechance.faith.api.dto.nearestMosque.toMosqueResponse
import net.thechance.faith.exception.InvalidRequestParameterException
import net.thechance.faith.service.MosqueService
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
        @RequestParam keyword: String
    ): ResponseEntity<List<MosqueResponse>> {
        if (keyword.isBlank()) {
            throw InvalidRequestParameterException("Parameter 'keyword' must not be blank.")
        }

        val mosques = mosqueService.searchMosquesByName(keyword)
        val response = mosques.map { it.toMosqueResponse() }

        return ResponseEntity.ok(response)
    }
}
