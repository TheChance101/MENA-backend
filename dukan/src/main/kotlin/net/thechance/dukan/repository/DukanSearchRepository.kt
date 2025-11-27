package net.thechance.dukan.repository

import net.thechance.dukan.search.document.DukanDocument
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface DukanSearchRepository : ElasticsearchRepository<DukanDocument, String> {
    @Query(
        """
{
  "bool": {
    "must": [
      {
        "wildcard": {
          "name": {
            "value": "*?0*",
            "case_insensitive": true
          }
        }
      }
    ],
    "filter": [
        {"term": {"activationStatus": "ACTIVATED"}},
        {"term": {"status": "APPROVED"}}
        
    ]
  }
}
"""
    )
    fun searchByNameLike(query: String, pageable: Pageable): Page<DukanDocument>

    @Query(
        """
{
  "bool": {
    "must": [
      {
        "wildcard": {
          "name": {
            "value": "*?0*",
            "case_insensitive": true
          }
        }
      }
    ],
    "filter": [
        {"term": {"activationStatus": "ACTIVATED"}},
        {"term": {"status": "APPROVED"}},
        { "term": { "categoryIds": "?1" } }
    ]
  }
}
"""
    )
    fun searchByNameAndCategory(
        name: String,
        categoryId: String,
        pageable: Pageable
    ): Page<DukanDocument>
}