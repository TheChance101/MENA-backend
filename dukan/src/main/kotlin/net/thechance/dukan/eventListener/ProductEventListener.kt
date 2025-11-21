package net.thechance.dukan.eventListener

import net.thechance.dukan.repository.DukanProductSearchRepository
import net.thechance.dukan.search.document.ProductDocument
import net.thechance.events.dukan.ProductSearchEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ProductEventListener(
    private val productSearchRepository: DukanProductSearchRepository
) {
    @EventListener
    @Async
    fun onProductEventListener(event: ProductSearchEvent){
        when(event){
            is ProductSearchEvent.Delete -> productSearchRepository.deleteById(event.id)
            is ProductSearchEvent.Save -> saveProductDocument(event)
        }
    }

    private fun saveProductDocument(event: ProductSearchEvent.Save) {
        val productDocument = ProductDocument(
            id = event.id,
            name = event.name,
            price = event.price,
            dukanName = event.dukanName,
            dukanId = event.dukanId,
            mainImageUrl = event.mainImageUrl,
            shelfName = event.shelfName
        )
        productSearchRepository.save(productDocument)
    }
}