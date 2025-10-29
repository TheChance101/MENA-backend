package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.service.exception.DukanProductCreationFailedException
import net.thechance.dukan.service.exception.ProductNameAlreadyTakenException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
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
            checkProductNameExistence(dukan.id, params.name)
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

    @Transactional
    fun updateProduct(
        productId: UUID,
        updateParams: DukanProductUpdateParams
    ): UUID {
        val product = dukanProductRepository
            .findByIdAndDukan_OwnerId(productId, updateParams.ownerId)
            .orElseThrow { ProductNotFoundException() }

        if (product.name != updateParams.name) {
            checkProductNameExistence(product.dukan.id, updateParams.name)
        }

        val shelf = dukanShelfRepository.getReferenceById(updateParams.shelfId)

        deleteUnusedProductImages(product.imageUrls, updateParams.imageUrls)

        val updatedProduct = product.copy(
            name = updateParams.name.trim(),
            price = updateParams.price,
            imageUrls = updateParams.imageUrls,
            description = updateParams.description.trim(),
            shelf = shelf
        )

        return dukanProductRepository.save(updatedProduct).id
    }

    @Transactional
    fun uploadProductImage(
        ownerId: UUID,
        productId: UUID,
        file: MultipartFile
    ): String {
        val product = dukanProductRepository
            .findByIdAndDukan_OwnerId(productId, ownerId)
            .orElseThrow { ProductNotFoundException() }
        val imageUrl = imageStorageService.uploadImage(
            file = file,
            fileName = product.name,
            folderName = PRODUCT_FOLDER_NAME
        )
        return imageUrl
    }

    private fun checkProductNameExistence(dukanId: UUID, name: String) {
        if (dukanProductRepository.existsByDukanIdAndNameIgnoreCase(dukanId, name)) {
            throw ProductNameAlreadyTakenException()
        }
    }

    private fun deleteUnusedProductImages(oldImageUrls: List<String>, updatedImageUrls: List<String>) {
        try {
            oldImageUrls
                .filterNot { it in updatedImageUrls }
                .forEach {
                    if (imageStorageService.deleteImage(it).not()) {
                        // TODO save failed images table and try to delete them later
                    }
                }
        } catch (_: Exception) {
            // TODO save failed images table and try to delete them later
        }
    }


    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
    }
}