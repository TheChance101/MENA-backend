package net.thechance.dukan.search.repository

import net.thechance.dukan.search.document.DukanDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface DukanSearchRepository :ElasticsearchRepository<DukanDocument,String>{
    fun findByNameContaining(query:String):List<DukanDocument>
}