package net.thechance.dukan.service.model

import java.math.BigDecimal

data class ProductSearchResultPreview(
    val id:String,
    val name :String,
    val dukanName:String,
    val dukanId:String,
    val price: BigDecimal,
    val mainImageUrl:String,
    val isFavorite:Boolean = false
)
