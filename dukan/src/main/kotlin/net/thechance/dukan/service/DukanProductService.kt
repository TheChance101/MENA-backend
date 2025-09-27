package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.exception.dukan_product.ProductNotFoundException
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.DukanProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID
import java.util.*


@Service
class DukanProductService(
    private val dukanProductRepository: DukanProductRepository,
    private val imageStorageService: ImageStorageService,
) {
    @Transactional
    fun uploadProductImages(productId: UUID, files: List<MultipartFile>): List<String> {
        val product = dukanProductRepository.findById(productId)
            .orElseThrow {
                ProductNotFoundException()
            }
        val imageUrls = mutableListOf<String>()
        files.forEach { file ->
            val imageUrl = imageStorageService.uploadImage(
                file = file,
                fileName = "${product.name}-${file.originalFilename}",
                folderName = PRODCUT_FOLDER_NAME
            )
            imageUrls.add(imageUrl)
        }
        dukanProductRepository.save(product.copy(imageUrls = imageUrls))
        return imageUrls
    }

    fun getProductsByShelf(shelfId: UUID, pageable: Pageable): Page<DukanProduct> {
        return dukanProductRepository.findAllByShelfId(shelfId, pageable)
    }

    companion object {
        private val PRODCUT_FOLDER_NAME = "product"
    }
}