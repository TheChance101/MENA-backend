package net.thechance.faith.api.dto.tilawah

import net.thechance.faith.entity.Reciter

private fun Reciter.toResponse(): ReciterResponse = ReciterResponse(
    id = id,
    name = name,
    arabicName = arabicName,
    tilawahType = tilawahType
)

fun List<Reciter>.toResponse(): List<ReciterResponse> = this.map { it.toResponse() }
