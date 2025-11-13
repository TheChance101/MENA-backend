package net.thechance.faith.api.controller

import jakarta.validation.Valid
import net.thechance.faith.api.dto.tilawah.AyahSoundRequest
import net.thechance.faith.api.dto.tilawah.ReciterResponse
import net.thechance.faith.api.dto.tilawah.SurahSoundRequest
import net.thechance.faith.api.dto.tilawah.toResponse
import net.thechance.faith.service.tilawah.TilawahService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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
        @Valid request: AyahSoundRequest,
    ): ResponseEntity<String> {
        val soundUrl = recitersService.getAyahSoundUrl(
            reciterId = request.reciterId,
            surahNumber = request.surahNumber,
            ayahNumber = request.ayahNumber
        )
        return ResponseEntity.ok(soundUrl)
    }

    @GetMapping("/surah/sound")
    fun getSurahSoundUrl(
        @Valid request: SurahSoundRequest,
    ): ResponseEntity<String> {
        val soundUrl = recitersService.getSurahSoundsUrl(
            reciterId = request.reciterId,
            surahNumber = request.surahNumber,
        )
        return ResponseEntity.ok(soundUrl)
    }
}
