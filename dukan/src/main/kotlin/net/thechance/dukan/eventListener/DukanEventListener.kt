package net.thechance.dukan.eventListener

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanProductSearchRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.DukanSearchRepository
import net.thechance.dukan.search.mpper.toDocument
import net.thechance.events.dukan.DukanSearchEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DukanEventListener(
    private val dukanRepository: DukanRepository,
    private val dukanSearchRepository: DukanSearchRepository,
    private val productRepository: DukanProductRepository,
    private val productSearchRepository: DukanProductSearchRepository
) {

    @EventListener
    @Async
    fun onDukanSearchEvent(event: DukanSearchEvent) {
        when (event.index) {
            DukanSearchEvent.SearchIndex.DUKAN_INDEX -> handleDukanSearchEvent(event.id, event.action)
            DukanSearchEvent.SearchIndex.PRODUCT_INDEX -> handleProductSearchEvent(event.id, event.action)
        }
    }

    private fun handleDukanSearchEvent(dukanId: UUID, action: DukanSearchEvent.Action) {
        when (action) {
            DukanSearchEvent.Action.SAVE -> saveDukanDocument(dukanId)
            DukanSearchEvent.Action.DELETE -> dukanSearchRepository.deleteById(dukanId.toString())
        }
    }

    private fun saveDukanDocument(dukanId: UUID) {
        val dukan = dukanRepository.findById(dukanId).orElse(null) ?: return
        val hasShelfs = dukan.shelves.isNotEmpty()
        val isApproved = dukan.status == Dukan.Status.APPROVED
        if (hasShelfs && isApproved) dukanSearchRepository.save(dukan.toDocument())
    }

    private fun handleProductSearchEvent(productId: UUID, action: DukanSearchEvent.Action) {
        when (action) {
            DukanSearchEvent.Action.SAVE -> saveProductDocument(productId)
            DukanSearchEvent.Action.DELETE -> productSearchRepository.deleteById(productId.toString())
        }
    }

    private fun saveProductDocument(productId: UUID) {
        val product = productRepository.findById(productId).orElse(null) ?: return
        productSearchRepository.save(product.toDocument())
    }
}