package net.thechance.events.dukan

import net.thechance.events.MenaEvent

sealed class DukanEvent : MenaEvent {
    data class Save(
        val id: String,
        val name: String,
        val status: Status,
        val imageUrl:String?,
        val lat:Double,
        val lng:Double
    ):DukanEvent() {
        enum class Status {
            APPROVED,
            REJECTED,
            PENDING,
        }
    }
    data class Delete(val id:String):DukanEvent()
}

