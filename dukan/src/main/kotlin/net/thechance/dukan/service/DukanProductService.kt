package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.service.exception.DukanProductCreationFailedException
import net.thechance.dukan.service.exception.ProductNameAlreadyTakenException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
import net.thechance.dukan.service.exception.ProductUpdateFailedException
import net.thechance.dukan.service.model.DukanProductCreationParams
import net.thechance.dukan.service.model.DukanProductUpdateParams
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.lang.Exception
import java.util.UUID


@Service
class DukanProductService(
    private val dukanProductRepository: DukanProductRepository,
    private val dukanShelfRepository: DukanShelfRepository,
    private val dukanService: DukanService,
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
            val shelf = dukanShelfRepository.getReferenceById(params.shelfId)
            existingProductByDukanIdAndName(dukan.id, params.name)
            val product = dukanProductRepository.save(
                DukanProduct(
                    name = params.name.trim(),
                    shelf = shelf,
                    dukan = dukan,
                    price = params.price,
                    description = params.description.trim(),
                    imageUrls = emptyList() // Images will be uploaded using a different endpoint
                )
            )
            return product.id
        } catch (_: EntityNotFoundException) {
            throw DukanProductCreationFailedException()
        }
    }

    fun getProductsByShelf(shelfId: UUID, pageable: Pageable): Page<DukanProduct> {
        return dukanProductRepository.findAllByShelfId(shelfId, pageable)
    }

    fun getProductById(productId: UUID): DukanProduct {
        return dukanProductRepository.findById(productId).orElseThrow {
            ProductNotFoundException()
        }
    }

    fun updateProduct(
        productId: UUID, params: DukanProductUpdateParams
    ): DukanProduct {
        val product = dukanProductRepository
            .findByIdAndDukan_OwnerId(productId, params.ownerId)
            .orElseThrow {
                ProductNotFoundException()
            }

        if (product.name != params.name) {
            existingProductByDukanIdAndName(product.dukan.id, params.name)
        }

        val shelf = dukanShelfRepository.getReferenceById(params.shelfId)

        product.imageUrls
            .filterNot { it in params.imageUrls }
            .forEach { imageStorageService.deleteImage(it) }

        return product.copy(
            name = params.name.trim(),
            price = params.price,
            imageUrls = params.imageUrls,
            description = params.description.trim(),
            shelf = shelf
        ).also { dukanProductRepository.save(it) }
    }

    private fun existingProductByDukanIdAndName(dukanId: UUID, name: String) {
        if (dukanProductRepository.existsByDukanIdAndNameIgnoreCase(dukanId, name)) {
            throw ProductNameAlreadyTakenException()
        }
    }


    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
    }
}