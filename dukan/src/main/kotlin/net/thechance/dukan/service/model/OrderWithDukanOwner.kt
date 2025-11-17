package net.thechance.dukan.service.model

import net.thechance.dukan.entity.Order

data class OrderWithDukanOwner(
    val order: Order,
    val isDukanOwner: Boolean
)
