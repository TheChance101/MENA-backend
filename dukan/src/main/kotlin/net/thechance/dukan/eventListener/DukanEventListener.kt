package net.thechance.dukan.eventListener

import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanProductSearchRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.DukanSearchRepository
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.dukan.search.document.ProductDocument
import net.thechance.dukan.search.mpper.toDocument
import net.thechance.events.dukan.DukanEvent
import net.thechance.events.dukan.DukanSearchEvent
import net.thechance.events.dukan.ProductEvent
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
    fun onDukanSearchEvent(event: DukanEvent) {
        when(event){
            is DukanEvent.Delete -> dukanSearchRepository.deleteById(event.id)
            is DukanEvent.Save -> saveDukanDocument(event)
        }
    }

    @EventListener
    @Async
    fun onProductEventListener(event: ProductEvent){
        when(event){
            is ProductEvent.Delete -> productSearchRepository.deleteById(event.id)
            is ProductEvent.Save -> saveProductDocument(event)
        }
    }


    private fun saveDukanDocument(event: DukanEvent.Save) {
        val dukanDocument = DukanDocument(
            id = event.id,
            name = event.name,
            status = event.status.toDukanStatus(),
            imageUrl = event.imageUrl,
            location = event.location
        )
         dukanSearchRepository.save(dukanDocument)
    }


    private fun saveProductDocument(event: ProductEvent.Save) {
        val productDocument = ProductDocument(
            id = event.id,
            name = event.name,
            price = event.price,
            description = event.description,
            mainImageUrl = event.mainImageUrl,
            shelfName = event.shelfName
        )
        productSearchRepository.save(productDocument)
    }
}