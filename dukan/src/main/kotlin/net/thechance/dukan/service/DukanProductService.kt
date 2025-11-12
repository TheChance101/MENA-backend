package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.entity.*
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
import net.thechance.dukan.repository.FavoriteProductRepository
import net.thechance.dukan.service.exception.DukanProductCreationFailedException
import net.thechance.dukan.service.exception.InvalidDiscountException
import net.thechance.dukan.service.exception.ProductNameAlreadyTakenException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.service.model.DukanProductCreationParams
import net.thechance.dukan.service.model.DukanProductUpdateParams
import net.thechance.dukan.service.model.DukanProductWithFavoriteAndQuantity
import net.thechance.events.dukan.DukanEvent
import net.thechance.events.dukan.ProductEvent
import net.thechance.events.publisher.MenaEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Service
class DukanProductService(
    private val dukanProductRepository: DukanProductRepository,
    private val favoriteProductRepository: FavoriteProductRepository,
    private val dukanShelfRepository: DukanShelfRepository,
    private val dukanService: DukanService,
    private val imageStorageService: ImageStorageService,
    private val eventPublisher: MenaEventPublisher
) {
    @Transactional
    fun uploadProductImages(productId: UUID, files: List<MultipartFile>): List<String> {
        val product: DukanProduct = dukanProductRepository.findById(productId)
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
            dukanProductRepository.delete(product).also {
                eventPublisher.publish(
                    ProductEvent.Delete(product.id.toString())
                )
            }

            throw e
        }
        dukanProductRepository.save(product.copy(imageUrls = imageUrls)).also { product ->
            eventPublisher.publish(
                event = product.toProductSaveEvent()
            )
            if (product.dukan.shelves.isNotEmpty() && product.dukan.status == Dukan.Status.APPROVED) {
                eventPublisher.publish(
                    product.dukan.toDukanSaveEvent()
                )
            }
        }
        return imageUrls
    }

    private fun Dukan.toDukanSaveEvent() = DukanEvent.Save(
        id = this.id.toString(),
        name = this.name,
        imageUrl = this.imageUrl,
        status = DukanEvent.Save.Status.APPROVED,
        lat = this.latitude,
        lng = this.longitude
    )

    private fun DukanProduct.toProductSaveEvent() = ProductEvent.Save(
        id = this.id.toString(),
        name = this.name,
        dukanName = this.description,
        dukanId = this.dukan.id.toString(),
        mainImageUrl = this.imageUrls.firstOrNull().orEmpty(),
        price = this.price.final,
        shelfName = this.shelf.title
    )

    fun createProduct(params: DukanProductCreationParams): UUID {
        try {
            val dukan = dukanService.getDukanByOwnerId(params.ownerId)
            val shelf = dukanShelfRepository.getReferenceById(params.shelfId)
            checkProductNameExistence(dukan.id, params.name)

            val discountValue = calculateDiscount(params.price)
            val product = dukanProductRepository.save(
                DukanProduct(
                    name = params.name.trim(),
                    shelf = shelf,
                    dukan = dukan,
                    price = params.price,
                    discount = discountValue,
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
    fun getProductsByShelf(userId: UUID, shelfId: UUID, pageable: Pageable): Page<DukanProductWithFavoriteAndQuantity> {
        val products = dukanProductRepository.findProductsWithFavoriteAndQuantityByShelf(userId, shelfId, pageable)
        return products
    }


    fun getProductsByShelf(shelfId: UUID, pageable: Pageable): Page<DukanProduct> {
        return dukanProductRepository.findAllByShelfId(shelfId, pageable)
    }

    @Transactional
    fun getProductById(userId: UUID, productId: UUID): DukanProductWithFavoriteAndQuantity {
        val product = dukanProductRepository.findProductWithFavoriteAndQuantityById(userId, productId)
        return product
    }

    @Transactional
    fun toggleFavoriteStatus(userId: UUID, productId: UUID): Boolean {
        return if (favoriteProductRepository.deleteByIdProductIdAndIdUserId(productId, userId) > 0) {
            false
        } else {
            createFavoriteEntry(userId, productId)
        }
    }

    private fun createFavoriteEntry(userId: UUID, productId: UUID): Boolean {
        val newFavorite = FavoriteProduct(
            id = FavoriteProductId(
                productId = productId,
                userId = userId
            )
        )
        favoriteProductRepository.save(newFavorite)
        return true
    }

    @Transactional
    fun updateProduct(
        updateParams: DukanProductUpdateParams
    ): UUID {
        val product = dukanProductRepository
            .findByIdAndDukanOwnerId(updateParams.productId, updateParams.ownerId)
            .orElseThrow { ProductNotFoundException() }
        if (product.name != updateParams.name) {
            checkProductNameExistence(product.dukan.id, updateParams.name)
        }

        val shelf = dukanShelfRepository.getReferenceById(updateParams.shelfId)

        deleteUnusedProductImages(product.imageUrls, updateParams.imageUrls)
        val updatedProduct = buildUpdatedProduct(product, updateParams, shelf)
        return dukanProductRepository.save(updatedProduct).id
    }

    @Transactional
    fun uploadProductImage(
        ownerId: UUID,
        productId: UUID,
        file: MultipartFile
    ): String {
        val product = dukanProductRepository
            .findByIdAndDukanOwnerId(productId, ownerId)
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

    private fun calculateDiscount(price: Price): BigDecimal {
        val basePrice = price.base
        val finalPrice = price.final
        if (finalPrice > basePrice) throw InvalidDiscountException()

        return (basePrice - finalPrice)
            .divide(basePrice, 10, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))
            .setScale(2, RoundingMode.HALF_UP)
    }

    private fun buildUpdatedProduct(
        product: DukanProduct,
        params: DukanProductUpdateParams,
        shelf: DukanShelf
    ): DukanProduct {
        val discountValue = calculateDiscount(params.price)
        return product.copy(
            name = params.name.trim(),
            price = params.price,
            discount = discountValue,
            imageUrls = params.imageUrls,
            description = params.description.trim(),
            shelf = shelf,
            isOutOfStock = params.isOutOfStock,
        )
    }

    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
    }
}