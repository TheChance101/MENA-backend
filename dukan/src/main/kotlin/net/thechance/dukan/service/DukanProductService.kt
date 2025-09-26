package net.thechance.dukan.service

import jakarta.transaction.Transactional
import net.thechance.dukan.exception.dukan_product.InvalidImageUrlFormatException
import net.thechance.dukan.exception.dukan_product.ProductNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
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
                fileName = "${product.name}-${Instant.now().toEpochMilli()}-${file.originalFilename}",
                folderName = PRODUCT_FOLDER_NAME
            )
            imageUrls.add(imageUrl)
        }
        checkImageValidity(imageUrls)
        dukanProductRepository.save(product.copy(imageUrls = imageUrls))
        return imageUrls
    }

    private fun checkImageValidity(imageUrls: List<String>) {
        imageUrls.forEach { url ->
            if (!url.matches(IMAGE_URL_PATTERN.toRegex())) {
                throw InvalidImageUrlFormatException()
            }
        }
    }

    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
        private const val IMAGE_URL_PATTERN = "^[a-zA-Z0-9/_.-]+$"
    }
}