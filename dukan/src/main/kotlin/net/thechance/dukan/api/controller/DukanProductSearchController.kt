package net.thechance.dukan.api.controller

import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.DukanProductSearchService
import net.thechance.dukan.service.model.DukanPreview
import net.thechance.dukan.service.model.ProductPreview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$DUKAN_PATH/products/search")
class DukanProductSearchController(
    private val searchService: DukanProductSearchService
) {

    @PostMapping("/seed")
    fun seed():ResponseEntity<String>{
        val totalDukanProductsIndexed = searchService.seed()
        return ResponseEntity.status(HttpStatus.CREATED).body("Indexed $totalDukanProductsIndexed")
    }



    @GetMapping
    fun search(
        @RequestParam query:String,
        pageable: Pageable
    ):ResponseEntity<Page<ProductPreview>>{
        return ResponseEntity.ok(searchService.search(query,pageable))
    }
}