package net.thechance.dukan.search.document

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.math.BigDecimal

@Document(indexName = "products")
data class ProductDocument(
    @Id val id: String,

    @Field(type = FieldType.Text)
    val name: String,

    @Field(type = FieldType.Double)
    val price: BigDecimal,

    @Field(type = FieldType.Text)
    val dukanName:String,

    @Field(type = FieldType.Text)
    val mainImageUrl:String,

    @Field(type = FieldType.Text)
    val shelfName:String
)
