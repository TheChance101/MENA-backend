package net.thechance.dukan.api.controller

import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.DukanSearchService
import net.thechance.dukan.service.model.DukanPreview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("$DUKAN_PATH/search")
class DukanSearchController (
    private val searchService: DukanSearchService
){
    @GetMapping
    fun search(
        @RequestParam query:String,
        pageable: Pageable
    ):ResponseEntity<Page<DukanPreview>>{
        return ResponseEntity.ok(searchService.search(query,pageable))
    }
}