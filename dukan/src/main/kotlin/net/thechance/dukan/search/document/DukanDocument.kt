package net.thechance.dukan.search.document

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "dukans")
data class DukanDocument(
    @Id val id:String,
    val name :String,
)