package net.thechance.events.dukan

import net.thechance.events.MenaEvent

sealed class DukanSearchEvent : MenaEvent {
    data class Save(
        val id: String,
        val name: String,
        val status: Status,
        val imageUrl:String?,
        val lat:Double,
        val lng:Double,
        val categoryIds:Set<String>,
        val activationStatus: ActivationStatus
    ):DukanSearchEvent() {
        enum class Status {
            APPROVED,
            REJECTED,
            PENDING,
        }
        enum class ActivationStatus {
            ACTIVATED,
            DEACTIVATED
        }
    }
    data class Delete(val id:String):DukanSearchEvent()
}

