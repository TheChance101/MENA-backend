package net.thechance.dukan.api.mapper.dukan

import net.thechance.dukan.api.dto.dukan.DukanStyleResponse
import net.thechance.dukan.entity.Dukan
import kotlin.enums.EnumEntries

fun EnumEntries<Dukan.Style>.toDukanStyleResponse(): DukanStyleResponse =
    DukanStyleResponse(styles = this.map { it.name })