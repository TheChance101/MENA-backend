package net.thechance.dukan.eventListener

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.repository.DukanSearchRepository
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.events.dukan.DukanStatusChangedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.*

@Component
class DukanStatusChangedListener(
    private val dukanSearchRepository: DukanSearchRepository
) {

    @EventListener
    @Async
    fun onStatusChanged(event: DukanStatusChangedEvent) {

        val optionalDoc: Optional<DukanDocument> = dukanSearchRepository.findById(event.dukanId.toString())
        if (optionalDoc.isEmpty) {
            return
        }
        val doc = optionalDoc.get()
        val updatedDoc = doc.copy(
            status = when (event.dukanStatus) {
                DukanStatusChangedEvent.Status.APPROVED -> Dukan.Status.APPROVED
                DukanStatusChangedEvent.Status.REJECTED -> Dukan.Status.REJECTED
                DukanStatusChangedEvent.Status.PENDING -> Dukan.Status.PENDING
            }
        )
        dukanSearchRepository.save(updatedDoc)
    }
}