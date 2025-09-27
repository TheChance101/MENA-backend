package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.exception.dukan_product.DukanProductCreationFailedException
import net.thechance.dukan.exception.dukan_product.ProductNameAlreadyTakenException
import net.thechance.dukan.exception.dukan_product.ProductNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.service.model.DukanProductCreationParams
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.lang.Exception
import java.util.UUID

@Service
class DukanProductService(
    private val dukanProductRepository: DukanProductRepository,
    private val dukanService: DukanService,
    private val dukanShelfService: DukanShelfService,
    private val imageStorageService: ImageStorageService,
) {
    @Transactional
    fun uploadProductImages(productId: UUID, files: List<MultipartFile>): List<String> {
        val product = dukanProductRepository.findById(productId)
            .orElseThrow {
                ProductNotFoundException()
            }
        val imageUrls = mutableListOf<String>()
        try {
            files.forEach { file ->
                val imageUrl = imageStorageService.uploadImage(
                    file = file,
                    fileName = "${product.name}-${file.originalFilename}",
                    folderName = PRODUCT_FOLDER_NAME
                )
                imageUrls.add(imageUrl)
            }
        } catch (e: Exception) {
            //Uploading the images is part of creating the product. If something went wrong while uploading the images,
            //We need to delete the product from the database.
            dukanProductRepository.delete(product)
            throw e
        }
        dukanProductRepository.save(product.copy(imageUrls = imageUrls))
        return imageUrls
    }

    fun createProduct(params: DukanProductCreationParams): UUID {
        try {
            val dukan = dukanService.getDukanByOwnerId(params.ownerId)
            val shelf = dukanShelfService.getShelfById(params.shelfId, params.ownerId)
            if (dukanProductRepository.existsByDukanIdAndNameIgnoreCase(dukan.id, params.name)) {
                throw ProductNameAlreadyTakenException()
            }
            val product = dukanProductRepository.save(
                DukanProduct(
                    name = params.name.trim(),
                    shelf = shelf,
                    dukan = dukan,
                    price = params.price,
                    description = params.description,
                    imageUrls = emptyList() // Images will be uploaded using a different endpoint
                )
            )
            return product.id
        } catch (_: EntityNotFoundException) {
            throw DukanProductCreationFailedException()
        }
    }

    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
    }
}