package net.thechance.dukan.api.dto

import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

data class DukanProductResponse(
    val id:UUID,
    val name:String,
    val shelfId:UUID,
    val price:Double,
    val description:String,
    val imageUrls:List<String>,
    val createdAt:Instant
)