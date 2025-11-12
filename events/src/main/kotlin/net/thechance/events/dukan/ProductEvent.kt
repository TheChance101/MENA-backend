package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.math.BigDecimal

sealed class ProductEvent: MenaEvent {
    data class Save(
        val id: String,
        val name: String,
        val price: BigDecimal,
        val dukanName: String,
        val dukanId:String,
        val mainImageUrl: String,
        val shelfName: String
    ):ProductEvent()

    data class Delete(val id:String):ProductEvent()
}
