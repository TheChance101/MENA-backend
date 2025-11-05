package net.thechance.dukan.search.mpper

import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.search.document.ProductDocument
import net.thechance.dukan.service.model.ProductPreview

fun DukanProduct.toDocument():ProductDocument{
    return ProductDocument(
        id = id.toString(),
        name = name,
        price = price,
        description = description,
        mainImageUrl = imageUrls.first(),
        shelfName = shelf.title
    )
}


fun ProductDocument.toSearchResultPreviewItem():ProductPreview{
    return ProductPreview(
        id = id,
        name = name,
        description = description,
        price = price,
        mainImageUrl = mainImageUrl
    )
}