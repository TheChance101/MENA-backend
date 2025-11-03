package net.thechance.events.dukan

import net.thechance.events.MenaEvent

sealed class ProductEvent: MenaEvent {
    data class Save(
        val id: String,
        val name: String,
        val price: Double,
        val description: String,
        val mainImageUrl: String,
        val shelfName: String
    ):ProductEvent()

    data class Delete(val id:String):ProductEvent()
}
