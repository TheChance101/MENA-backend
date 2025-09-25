package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.DukanProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*


@Service
class DukanProductService(
    private val productRepository: DukanProductRepository
) {

    fun getProductsByShelf(shelfId: UUID, pageable: Pageable): Page<DukanProduct> {
        return productRepository.findAllByShelfId(shelfId, pageable)
    }
}