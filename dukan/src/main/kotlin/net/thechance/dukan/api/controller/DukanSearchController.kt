package net.thechance.dukan.api.controller

import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.dukan.search.service.DukanSearchService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("$DUKAN_PATH/search")
class DukanSearchController (
    private val searchService: DukanSearchService
){

    @PostMapping("/seed")
    fun seed():ResponseEntity<String>{
        searchService.seed()
        return ResponseEntity.status(HttpStatus.CREATED).body("Indexed sample dukans")
    }


    @GetMapping
    fun search(
        @RequestParam query:String
    ):ResponseEntity<List<DukanDocument>>{
        return ResponseEntity.ok(searchService.search(query))
    }
}