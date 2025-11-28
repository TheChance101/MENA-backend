package net.thechance.dukan.api.dto.dukan

import net.thechance.dukan.entity.Dukan

data class DukanActivationStatusResponse(
    val activationStatus: Dukan.ActivationStatus,
    val reason : String? = null
)