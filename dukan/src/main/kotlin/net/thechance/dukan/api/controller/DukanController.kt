package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.DukanStyleResponse
import net.thechance.dukan.mapper.toDukanStyleResponse
import net.thechance.dukan.service.DukanService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dukan")
class DukanController(
    private val dukanService: DukanService
) {
    @GetMapping("/styles")
    fun getAllStyles(): ResponseEntity<DukanStyleResponse> {
        val styles = dukanService.getAllStyles().toDukanStyleResponse()
       return ResponseEntity.ok(styles)
    }
}