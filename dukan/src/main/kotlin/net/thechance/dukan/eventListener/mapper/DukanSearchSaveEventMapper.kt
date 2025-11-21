package net.thechance.dukan.eventListener.mapper

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanProduct
import net.thechance.events.dukan.DukanSearchEvent
import net.thechance.events.dukan.ProductSearchEvent

 fun Dukan.toDukanSaveEvent() = DukanSearchEvent.Save(
    id = this.id.toString(),
    name = this.name,
    imageUrl = this.imageUrl,
    status = when(this.status){
       Dukan.Status.APPROVED -> DukanSearchEvent.Save.Status.APPROVED
       Dukan.Status.REJECTED -> DukanSearchEvent.Save.Status.REJECTED
       Dukan.Status.PENDING -> DukanSearchEvent.Save.Status.PENDING
    },
    lat = this.latitude,
    lng = this.longitude,
    categoryIds = this.categories.map { it.id.toString() }.toSet(),
    activationStatus = when(this.activationStatus){
       Dukan.ActivationStatus.ACTIVATED -> DukanSearchEvent.Save.ActivationStatus.ACTIVATED
       Dukan.ActivationStatus.DEACTIVATED -> DukanSearchEvent.Save.ActivationStatus.DEACTIVATED
       null -> DukanSearchEvent.Save.ActivationStatus.DEACTIVATED
    }
)

 fun DukanProduct.toProductSaveEvent() = ProductSearchEvent.Save(
    id = this.id.toString(),
    name = this.name,
    dukanName = this.description,
    dukanId = this.dukan.id.toString(),
    mainImageUrl = this.imageUrls.firstOrNull().orEmpty(),
    price = this.price.final,
    shelfName = this.shelf.title
)