package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.exception.dukan_product.ProductNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class DukanProductService(
    private val dukanProductRepository: DukanProductRepository,
    private val imageStorageService: ImageStorageService,
) {
    @Transactional
    fun uploadProductImages(productId: UUID, files: List<MultipartFile>): List<String> {
        val product = dukanProductRepository.findByProductId(productId) ?: throw ProductNotFoundException()
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

    companion object {
        private val PRODCUT_FOLDER_NAME = "product"
    }
}