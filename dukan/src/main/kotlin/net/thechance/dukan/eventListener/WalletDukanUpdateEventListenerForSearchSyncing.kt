package net.thechance.dukan.eventListener

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.repository.DukanSearchRepository
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.events.dukan.DukanUpdateEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.*

@Component
class WalletDukanUpdateEventListenerForSearchSyncing (
    private val dukanSearchRepository: DukanSearchRepository
){

    @EventListener
    @Async
    fun onWalletDukanUpdated(event:DukanUpdateEvent){
        val optionalDoc: Optional<DukanDocument> = dukanSearchRepository.findById(event.dukanId.toString())
        if (optionalDoc.isEmpty) {
            return
        }
        val doc = optionalDoc.get()
        val updatedDoc = doc.copy(
            name = event.name,
            imageUrl = event.imageUrl,
            status = when(event.status){
                DukanUpdateEvent.Status.APPROVED -> Dukan.Status.APPROVED
                DukanUpdateEvent.Status.REJECTED -> Dukan.Status.REJECTED
                DukanUpdateEvent.Status.PENDING -> Dukan.Status.PENDING
            },
            activationStatus = when(event.activationStatus){
                DukanUpdateEvent.ActivationStatus.ACTIVATED -> Dukan.ActivationStatus.ACTIVATED
                DukanUpdateEvent.ActivationStatus.DEACTIVATED -> Dukan.ActivationStatus.DEACTIVATED
                null -> Dukan.ActivationStatus.DEACTIVATED
            }
        )
    }

}