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
import net.thechance.events.dukan.DukanSearchEvent
import net.thechance.events.publisher.MenaEventPublisher
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
    private val eventPublisher: MenaEventPublisher
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
            dukanProductRepository.delete(product).also {
                eventPublisher.publish(DukanSearchEvent(
                    id = productId,
                    index = DukanSearchEvent.SearchIndex.PRODUCT_INDEX,
                    action = DukanSearchEvent.Action.DELETE
                ))
            }

            throw e
        }
        dukanProductRepository.save(product.copy(imageUrls = imageUrls)).also {
            publishSearchEvents(product.id,product.dukan.id)
        }
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
            publishSearchEvents(product.id,product.dukan.id)
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

    private fun publishSearchEvents( productId: UUID,dukanId:UUID) {
        eventPublisher.publish(
            DukanSearchEvent(
                id = productId,
                index = DukanSearchEvent.SearchIndex.PRODUCT_INDEX,
                action = DukanSearchEvent.Action.SAVE
            )
        )
        eventPublisher.publish(
            DukanSearchEvent(
                id = dukanId,
                index = DukanSearchEvent.SearchIndex.DUKAN_INDEX,
                action = DukanSearchEvent.Action.SAVE
            )
        )
    }

    companion object {
        private const val PRODUCT_FOLDER_NAME = "product"
    }
}