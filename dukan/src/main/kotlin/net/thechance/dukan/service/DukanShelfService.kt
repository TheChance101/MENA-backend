package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanProduct
import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.exception.ShelfDeletionNotAllowedException
import net.thechance.dukan.exception.ShelfNameAlreadyTakenException
import net.thechance.dukan.exception.ShelfNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DukanShelfService(
    private val dukanShelfRepository: DukanShelfRepository,
    private val dukanService: DukanService,
    private val dukanProductRepository: DukanProductRepository
) {
    fun createShelf(title: String, ownerId: UUID): DukanShelf {
        val dukan = dukanService.getDukanByOwnerId(ownerId)

        if (dukanShelfRepository.existsByTitleAndDukanId(title, dukan.id)) {
            throw ShelfNameAlreadyTakenException()
        }

        return dukanShelfRepository.save(
            DukanShelf(
                title = title,
                dukan = dukan
            )
        )
    }
    fun deleteShelf(shelfId: UUID, ownerId: UUID) {
        val dukan = dukanService.getDukanByOwnerId(ownerId)

        val shelf = dukanShelfRepository.findByIdAndDukanId(shelfId, dukan.id)
            ?: throw ShelfNotFoundException()

        if (dukanProductRepository.existsByShelfId(shelfId)) {
            throw ShelfDeletionNotAllowedException()
        }

        dukanShelfRepository.delete(shelf)
    }
}