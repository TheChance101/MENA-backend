package net.thechance.faith.service

import net.thechance.faith.entity.Mosque
import net.thechance.faith.repository.MosqueRepository
import org.springframework.stereotype.Service

@Service
class MosqueService(private val mosqueRepository: MosqueRepository) {

    fun searchMosquesByName(keyword: String): List<Mosque> =
        mosqueRepository.searchMosquesByName(keyword)
}
