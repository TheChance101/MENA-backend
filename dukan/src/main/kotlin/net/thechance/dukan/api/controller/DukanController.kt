package net.thechance.dukan.api.controller

import jakarta.validation.constraints.NotBlank
import net.thechance.dukan.api.dto.DukanNameExistenceResponse
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

    @GetMapping("/exists")
    fun checkNameExists(
        @RequestParam(name = "name") @NotBlank name: String
    ): ResponseEntity<DukanNameExistenceResponse> {
        val exists = dukanService.doesNameExist(name)
        return ResponseEntity.ok(DukanNameExistenceResponse(exists))
    }
}