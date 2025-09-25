package net.thechance.dukan.api.dto

import java.util.UUID

data class DukanProductResponse(
    val id:UUID,
    val name:String,
    val shelfId:UUID,
    val dukanId:UUID,
    val price:Double,
    val description:String,
    val imageUrls:List<String>
)