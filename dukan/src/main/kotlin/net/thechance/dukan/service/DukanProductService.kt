package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.api.dto.product.DukanProductResponse
import net.thechance.dukan.api.mapper.product.toProductResponse
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.CartRepository
import net.thechance.dukan.service.exception.DukanProductCreationFailedException
import net.thechance.dukan.service.exception.ProductNameAlreadyTakenException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
import net.thechance.dukan.service.model.DukanProductCreationParams
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
    private val cartRepository: CartRepository
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
            if (dukanProductRepository.existsByDukanIdAndNameIgnoreCase(dukan.id, params.name)) {
                throw ProductNameAlreadyTakenException()
            }
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
    @Transactional
    fun getProductsByShelf(userId: UUID, shelfId: UUID, pageable: Pageable): Page<DukanProductResponse> {
        val productsPage = dukanProductRepository.findAllByShelfIdWithDukan(shelfId, pageable)
        val shelf = productsPage.content.firstOrNull()?.shelf
            ?: return productsPage.map { it.toProductResponse(0) }

        val cart = cartRepository.findByUserIdAndDukanIdWithItemsAndProducts(userId, shelf.dukan.id)
        val quantityMap = cart?.items?.associate { it.product.id to it.quantity } ?: emptyMap()

        return productsPage.map { product ->
            val quantity = quantityMap[product.id] ?: 0
            product.toProductResponse(quantity)
        }
    }
    @Transactional
    fun getProductById(userId: UUID, productId: UUID): DukanProductResponse {
        val product = dukanProductRepository.findByIdWithDukan(productId)
        val cart = cartRepository.findByUserIdAndDukanIdWithItemsAndProducts(userId, product.shelf.dukan.id)
        val quantity = cart?.items?.find { it.product.id == product.id }?.quantity ?: 0

        return product.toProductResponse(quantity)
    }

    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
    }
}