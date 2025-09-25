package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.exception.ShelfDeletionNotAllowedException
import net.thechance.dukan.exception.ShelfNameAlreadyTakenException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.DukanShelfRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DukanShelfService(
    private val dukanShelfRepository: DukanShelfRepository,
    private val dukanRepository: DukanRepository,
    private val dukanProductRepository: DukanProductRepository
) {
    fun createShelf(title: String, ownerId: UUID): DukanShelf {
        val dukan = dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()

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
        val dukan = dukanRepository.findByOwnerId(ownerId) ?: throw DukanNotFoundException()

        val shelf = dukanShelfRepository.findByIdAndDukanId(shelfId, dukan.id)
            ?: throw DukanNotFoundException()

        val products = dukanProductRepository.findAllByShelfId(shelf.id)
        if (products.isNotEmpty()) {
            throw ShelfDeletionNotAllowedException()
        }

        dukanShelfRepository.delete(shelf)
    }
}