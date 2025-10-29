package net.thechance.events.dukan

import net.thechance.events.MenaEvent
import java.util.UUID

data class DukanSearchEvent(
    val id: UUID,
    val index:SearchIndex,
    val action: Action
) : MenaEvent {
    enum class Action {
        SAVE,
        DELETE
    }
    enum class SearchIndex{
        DUKAN_INDEX,
        PRODUCT_INDEX
    }
}

