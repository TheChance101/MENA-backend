package net.thechance.dukan.search.service

import net.thechance.dukan.search.document.DukanDocument
import net.thechance.dukan.search.repository.DukanSearchRepository
import org.springframework.stereotype.Service

@Service
class DukanSearchService(
    private val searchRepository: DukanSearchRepository
) {
    fun seed(){
        searchRepository.save(
            DukanDocument(
                id = "1",
                name = "test dukan",
            )
        )
    }

    fun search(query:String):List<DukanDocument> = searchRepository.findByNameContaining(query)
}