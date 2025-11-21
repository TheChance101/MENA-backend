package net.thechance.dukan.eventListener

import net.thechance.dukan.repository.DukanSearchRepository
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.events.dukan.DukanSearchEvent
import org.springframework.context.event.EventListener
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DukanEventListener(
    private val dukanSearchRepository: DukanSearchRepository,
) {

    @EventListener
    @Async
    fun onDukanSearchEvent(event: DukanSearchEvent) {
        when(event){
            is DukanSearchEvent.Delete -> dukanSearchRepository.deleteById(event.id)
            is DukanSearchEvent.Save -> saveDukanDocument(event)
        }
    }

    private fun saveDukanDocument(event: DukanSearchEvent.Save) {
        val dukanDocument = DukanDocument(
            id = event.id,
            name = event.name,
            status = event.status.toDukanStatus(),
            imageUrl = event.imageUrl,
            categoryIds = event.categoryIds,
            location = GeoPoint(event.lat,event.lng),
            activationStatus = event.activationStatus.toDukanActivationStatus(),

        )
         dukanSearchRepository.save(dukanDocument)
    }
}