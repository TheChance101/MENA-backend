package net.thechance.dukan.api.dto

import net.thechance.dukan.entity.Dukan

data class DukanStatuesResponse(
    val dukanStatus: Dukan.Status
)
