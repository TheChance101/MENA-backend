package net.thechance.dukan.search.mpper

import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.search.document.ProductDocument
import net.thechance.dukan.service.model.ProductSearchResultPreview

fun DukanProduct.toDocument():ProductDocument{
    return ProductDocument(
        id = id.toString(),
        name = name,
        price = price.final,
        dukanName = dukan.name,
        mainImageUrl = imageUrls.first(),
        shelfName = shelf.title
    )
}


fun ProductDocument.toSearchResultPreviewItem(isFavorite:Boolean = false):ProductSearchResultPreview{
    return ProductSearchResultPreview(
        id = id,
        name = name,
        dukanName = dukanName,
        price = price,
        mainImageUrl = mainImageUrl,
        isFavorite = isFavorite
    )
}