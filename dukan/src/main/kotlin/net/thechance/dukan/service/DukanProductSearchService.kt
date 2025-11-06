package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanProductSearchRepository
import net.thechance.dukan.repository.FavoriteProductRepository
import net.thechance.dukan.search.mpper.toDocument
import net.thechance.dukan.search.mpper.toSearchResultPreviewItem
import net.thechance.dukan.service.model.ProductSearchResultPreview
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
    private val favoriteProductRepository: FavoriteProductRepository
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

        fun search(userId: UUID, query: String, pageable: Pageable): Page<ProductSearchResultPreview> {
        val productDocs = searchRepository.searchByNameLike(query, pageable)

        val productIds = productDocs.content.map { UUID.fromString(it.id) }

        val favoriteIds = favoriteProductRepository
            .findAllByIdUserIdAndIdProductIdIn(userId, productIds)
            .map { it.id.productId }
            .toSet()

        return productDocs.map { doc ->
            doc.toSearchResultPreviewItem(isFavorite = favoriteIds.contains(UUID.fromString(doc.id)))
        }
    }
}