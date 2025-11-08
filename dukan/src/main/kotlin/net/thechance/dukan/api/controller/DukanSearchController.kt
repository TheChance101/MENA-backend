package net.thechance.dukan.api.controller

import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.DukanSearchService
import net.thechance.dukan.service.model.DukanPreview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID


@RestController
@RequestMapping("$DUKAN_PATH/search")
class DukanSearchController (
    private val searchService: DukanSearchService
){
    @GetMapping
    fun search(
        @AuthenticationPrincipal userId:UUID,
        @RequestParam query:String,
        pageable: Pageable
    ):ResponseEntity<Page<DukanPreview>>{
        return ResponseEntity.ok(searchService.search(userId,query,pageable))
    }
}