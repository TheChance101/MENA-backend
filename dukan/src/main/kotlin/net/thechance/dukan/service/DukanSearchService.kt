package net.thechance.dukan.service

import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.DukanSearchRepository
import net.thechance.dukan.repository.FavoriteDukanRepository
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.dukan.search.mpper.toDocument
import net.thechance.dukan.search.mpper.toSearchResultPreviewItem
import net.thechance.dukan.service.model.DukanPreview
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class DukanSearchService(
    private val searchRepository: DukanSearchRepository,
    private val dukanRepository: DukanRepository,
    private val favoriteDukanRepository: FavoriteDukanRepository
) {

    fun indexIsEmpty(): Boolean = searchRepository.count() == 0L

    @Transactional(readOnly = true)
    fun seed(): Int {
        val pageSize = 100
        var page = 0
        var totalIndexed = 0

        while (true) {
            val dukansPage = dukanRepository.findAllApprovedWithShelvesAndProducts(PageRequest.of(page, pageSize))
            if (dukansPage.isEmpty) break

            val documents = dukansPage.content.map { it.toDocument() }
            searchRepository.saveAll(documents)
            totalIndexed += documents.size
            page++
        }

        return totalIndexed
    }

    fun search(userId: UUID, query: String, pageable: Pageable): Page<DukanPreview> {
        val dukanDocs: Page<DukanDocument> = searchRepository.searchByNameLike(query, pageable)
        val dukansIds = dukanDocs.content.map { UUID.fromString(it.id) }

        val favoriteIds: Set<UUID> = favoriteDukanRepository.findByIdUserIdAndIdDukanIdIn(userId, dukansIds)
            .map { it.id.dukanId }
            .toSet()

        return dukanDocs.map { doc->
            doc.toSearchResultPreviewItem(isFavorite = favoriteIds.contains(UUID.fromString(doc.id)))
        }
    }

}