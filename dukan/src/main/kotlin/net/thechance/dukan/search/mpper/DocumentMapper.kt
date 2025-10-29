package net.thechance.dukan.search.mpper

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.search.document.DukanDocument
import net.thechance.dukan.service.model.DukanPreview
import org.springframework.data.elasticsearch.core.geo.GeoPoint

fun Dukan.toDocument():DukanDocument{
    return DukanDocument(
        id = id.toString(),
        name = name,
        status = status,
        imageUrl = imageUrl,
        location = GeoPoint(latitude,longitude)
    )
}


fun DukanDocument.toSearchResultPreviewItem():DukanPreview{
    return DukanPreview(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}