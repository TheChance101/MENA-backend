package net.thechance.dukan.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
import net.thechance.dukan.entity.FavoriteProduct
import net.thechance.dukan.service.exception.DukanProductCreationFailedException
import net.thechance.dukan.service.exception.ProductNameAlreadyTakenException
import net.thechance.dukan.service.exception.ProductNotFoundException
import net.thechance.dukan.repository.FavoriteProductRepository
import net.thechance.dukan.service.model.DukanProductCreationParams
import net.thechance.events.publisher.MenaEventPublisher
import net.thechance.dukan.service.model.DukanProductUpdateParams
import net.thechance.events.dukan.DukanEvent
import net.thechance.events.dukan.ProductEvent
import net.thechance.dukan.service.model.DukanProductWithFavorite
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
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
        dukanProductRepository.save(product.copy(imageUrls = imageUrls)).also {product->
            eventPublisher.publish(
                event = product.toProductSaveEvent()
            )
            if (product.dukan.shelves.isNotEmpty() && product.dukan.status == Dukan.Status.APPROVED){
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
        description = this.description,
        mainImageUrl = this.imageUrls.firstOrNull().orEmpty(),
        price = this.price,
        shelfName = this.shelf.title
    )

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

    @Transactional
    fun getProductsByShelf(userId: UUID, shelfId: UUID, pageable: Pageable): Page<DukanProductWithFavorite> {
        val products = getProductsByShelfWithDukan(shelfId, pageable)
        val quantities = getProductQuantities(userId, shelfId)
        val favoriteProductIds = getFavoriteProductIds(userId, products.content)

        return mapProductsToResponse(products, quantities, favoriteProductIds)
    }

    private fun getProductsByShelfWithDukan(shelfId: UUID, pageable: Pageable): Page<DukanProduct> {
        return dukanProductRepository.findAllByShelfIdWithDukan(shelfId, pageable)
    }

    private fun getProductQuantities(userId: UUID, shelfId: UUID): Map<UUID, Int> {
        return dukanProductRepository.findProductQuantitiesByUserAndShelf(userId, shelfId)
            .associate { UUID.fromString(it[0].toString()) to (it[1] as Number).toInt() }
    }

    private fun getFavoriteProductIds(userId: UUID, products: List<DukanProduct>): Set<UUID> {
        val productIds = products.map { it.id }
        if (productIds.isEmpty()) return emptySet()

        return favoriteProductRepository.findAllByUserIdAndProductIdIn(userId, productIds)
            .map { it.productId }
            .toSet()
    }

    private fun mapProductsToResponse(
        products: Page<DukanProduct>,
        quantities: Map<UUID, Int>,
        favoriteProductIds: Set<UUID>
    ): Page<DukanProductWithFavorite> {
        return products.map { product ->
            DukanProductWithFavorite(
                product = product.apply { tempQuantity = quantities[product.id] ?: 0 },
                isFavorite = product.id in favoriteProductIds
            )
        }
    }

    @Transactional
    fun getProductById(userId: UUID, productId: UUID): DukanProduct {
        val product = dukanProductRepository.findByIdWithDukan(productId)
        val quantity = dukanProductRepository.findProductQuantitiesByUserAndShelf(userId, product.shelf.id)
            .firstOrNull { UUID.fromString(it[0].toString()) == product.id }
            ?.let { (it[1] as Number).toInt() } ?: 0
        product.tempQuantity = quantity
        return product
    }

    fun isProductFavorite(userId: UUID, productId: UUID): Boolean {
        return favoriteProductRepository.findByUserIdAndProductId(userId, productId) != null
    }

    fun toggleFavoriteStatus(userId: UUID, productId: UUID): Boolean {
        val favorite = favoriteProductRepository.findByUserIdAndProductId(userId, productId)
        return if (favorite != null) {
            favoriteProductRepository.deleteById(favorite.id)
            false
        } else {
            createFavoriteEntry(userId, productId)
        }
    }

    private fun createFavoriteEntry(userId: UUID, productId: UUID): Boolean {
        val newFavorite = FavoriteProduct(
            userId = userId,
            productId = productId
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


    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
    }
}