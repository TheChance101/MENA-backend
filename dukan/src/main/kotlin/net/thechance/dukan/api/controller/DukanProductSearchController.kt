package net.thechance.dukan.api.controller

import net.thechance.dukan.api.utils.EndPoints.DUKAN_PATH
import net.thechance.dukan.service.DukanProductSearchService
import net.thechance.dukan.service.model.ProductSearchResultPreview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("$DUKAN_PATH/products/search")
class DukanProductSearchController(
    private val searchService: DukanProductSearchService
) {
    @GetMapping
    fun search(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam query:String,
        pageable: Pageable
    ):ResponseEntity<Page<ProductSearchResultPreview>>{
        return ResponseEntity.ok(searchService.search(userId,query,pageable))
    }
}