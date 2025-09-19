package net.thechance.dukan.mapper

import net.thechance.dukan.api.dto.DukanStyleResponse
import net.thechance.dukan.entity.Dukan
import kotlin.enums.EnumEntries

fun EnumEntries<Dukan.Style>.toDukanStyleResponse(): DukanStyleResponse =
    DukanStyleResponse(styles = this.map { it.name })