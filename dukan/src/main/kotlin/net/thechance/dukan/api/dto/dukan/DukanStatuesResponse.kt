package net.thechance.dukan.api.dto.dukan

import net.thechance.dukan.entity.Dukan

data class DukanStatuesResponse(
    val name: String,
    val statues: Dukan.Status
)