package net.thechance.faith.api.controller

import net.thechance.faith.api.dto.reciter.ReciterResponse
import net.thechance.faith.api.dto.reciter.toResponse
import net.thechance.faith.service.RecitersService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("faith/tilawah/reciters")
class RecitersController(
    private val recitersService: RecitersService
) {

    @GetMapping
    fun getReciters(): ResponseEntity<List<ReciterResponse>> {
        val reciters = recitersService.getReciters()
        return ResponseEntity.ok(reciters.toResponse())
    }
}
