package net.thechance.dukan.api.controller

import jakarta.validation.constraints.NotBlank
import net.thechance.dukan.api.dto.DukanNameAvailabilityResponse
import net.thechance.dukan.service.DukanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dukan")
class DukanController(
    private val dukanService: DukanService,
) {

    @GetMapping("/available")
    fun checkNameAvailability(
        @RequestParam(name = "name") @NotBlank name: String
    ): ResponseEntity<DukanNameAvailabilityResponse> {
        val available = dukanService.isDukanNameAvailable(name)
        return ResponseEntity.ok(DukanNameAvailabilityResponse(available))
    }
}