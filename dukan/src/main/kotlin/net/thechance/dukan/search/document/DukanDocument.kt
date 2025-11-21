package net.thechance.dukan.search.document

import net.thechance.dukan.entity.Dukan
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.GeoPointField
import org.springframework.data.elasticsearch.core.geo.GeoPoint

@Document(indexName = "dukans")
data class DukanDocument(
    @Id val id: String,

    @Field(type = FieldType.Text)
    val name: String,

    @Field(type = FieldType.Keyword)
    val status: Dukan.Status,

    @Field(type = FieldType.Text)
    val imageUrl: String?,

    @GeoPointField
    val location: GeoPoint,

    @Field(type = FieldType.Keyword)
    val activationStatus: Dukan.ActivationStatus?,
)