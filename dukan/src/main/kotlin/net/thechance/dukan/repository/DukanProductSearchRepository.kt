package net.thechance.dukan.repository

import net.thechance.dukan.search.document.DukanDocument
import net.thechance.dukan.search.document.ProductDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.data.repository.query.Param

interface DukanProductSearchRepository:ElasticsearchRepository<ProductDocument,String> {
    @Query(
        """
    {
      "wildcard": {
        "name": {
          "value": "*?0*",
          "case_insensitive": true
        }
      }
    }
    """
    )
    fun searchByNameLike(@Param("query") query: String, pageable: Pageable): Page<ProductDocument>
}