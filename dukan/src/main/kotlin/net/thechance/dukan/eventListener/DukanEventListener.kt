package net.thechance.dukan.eventListener

import net.thechance.dukan.repository.DukanSearchRepository
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.events.dukan.DukanEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DukanEventListener(
    private val dukanSearchRepository: DukanSearchRepository,
) {

    @EventListener
    @Async
    fun onDukanSearchEvent(event: DukanEvent) {
        when(event){
            is DukanEvent.Delete -> dukanSearchRepository.deleteById(event.id)
            is DukanEvent.Save -> saveDukanDocument(event)
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
}