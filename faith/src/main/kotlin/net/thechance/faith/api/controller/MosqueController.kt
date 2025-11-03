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

    @GetMapping("/nearby")
    fun getNearbyMosques(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(defaultValue = "10.0") radiusKm: Double
    ): ResponseEntity<List<MosqueResponse>> {
        if (radiusKm <= 0) throw InvalidRequestParameterException("Parameter 'radiusKm' must be greater than 0.")

        val mosques = mosqueService.findNearby(latitude, longitude, radiusKm)
        val response = mosques.map { it.toMosqueResponse() }
        return ResponseEntity.ok(response)
    }
}
