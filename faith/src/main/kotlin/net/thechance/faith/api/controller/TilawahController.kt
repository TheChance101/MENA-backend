package net.thechance.faith.api.controller

import jakarta.validation.Valid
import net.thechance.faith.api.dto.tilawah.AyahSoundRequest
import net.thechance.faith.api.dto.tilawah.ReciterResponse
import net.thechance.faith.api.dto.tilawah.toResponse
import net.thechance.faith.service.tilawah.TilawahService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("faith/tilawah")
class TilawahController(
    private val recitersService: TilawahService
) {

    @GetMapping("/reciters")
    fun getReciters(): ResponseEntity<List<ReciterResponse>> {
        val reciters = recitersService.getReciters()
        return ResponseEntity.ok(reciters.toResponse())
    }

    @GetMapping("/ayah/sound")
    fun getAyahSoundUrl(
        @Valid @RequestBody ayahSoundRequest: AyahSoundRequest
    ): ResponseEntity<String> {
        val soundUrl = recitersService.getAyahSoundUrl(
            reciterId = ayahSoundRequest.reciterId,
            surahNumber = ayahSoundRequest.surahNumber,
            ayahNumber = ayahSoundRequest.ayahNumber
        )
        return ResponseEntity.ok(soundUrl)
    }
}
