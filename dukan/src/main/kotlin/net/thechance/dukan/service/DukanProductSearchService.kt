package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanProductSearchRepository
import net.thechance.dukan.repository.FavoriteDukanRepository
import net.thechance.dukan.search.mpper.toDocument
import net.thechance.dukan.search.mpper.toSearchResultPreviewItem
import net.thechance.dukan.service.model.ProductPreview
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DukanProductSearchService(
    private val searchRepository: DukanProductSearchRepository,
    private val dukanProductRepository: DukanProductRepository,
) {
    fun indexIsEmpty(): Boolean = searchRepository.count() == 0L

    @Transactional(readOnly = true)
    fun seed(): Int {
        val pageSize = 100
        var page = 0
        var totalIndexed = 0

        while (true) {
            val productsPage: Page<DukanProduct> = dukanProductRepository.findAll(PageRequest.of(page, pageSize))
            if (productsPage.isEmpty) break

            val documents = productsPage.content.map { it.toDocument() }
            searchRepository.saveAll(documents)
            totalIndexed += documents.size
            page++
        }
        return totalIndexed
    }

        fun search(query:String,pageable: Pageable):Page<ProductPreview> {
        return searchRepository.searchByNameLike(query,pageable).map { it.toSearchResultPreviewItem() }
    }

}