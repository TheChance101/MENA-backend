package net.thechance.dukan.service.model

data class DukanPreview(
    val id:String,
    val name:String,
    val imageUrl:String?,
    val isFavorite: Boolean = false
)
