package net.thechance.dukan.search.mpper

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.search.document.DukanDocument

fun Dukan.toDocument():DukanDocument{
    return DukanDocument(
        id = id.toString(),
        name = name
    )
}