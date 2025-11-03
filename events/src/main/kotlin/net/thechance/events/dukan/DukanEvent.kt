package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import org.springframework.data.elasticsearch.core.geo.GeoPoint

sealed class DukanEvent : MenaEvent {
    data class Save(
        val id: String,
        val name: String,
        val status: Status,
        val imageUrl:String?,
        val location: GeoPoint
    ):DukanEvent() {
        enum class Status {
            APPROVED,
            REJECTED,
            PENDING,
        }
    }
    data class Delete(val id:String):DukanEvent()
}

sealed class ProductEvent:MenaEvent{
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