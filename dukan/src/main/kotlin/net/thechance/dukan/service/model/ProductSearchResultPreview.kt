package net.thechance.dukan.service.model

data class ProductSearchResultPreview(
    val id:String,
    val name :String,
    val dukanName:String,
    val price:Double,
    val mainImageUrl:String,
    val isFavorite:Boolean = false
)
