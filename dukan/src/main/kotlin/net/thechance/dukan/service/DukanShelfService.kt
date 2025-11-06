package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.service.exception.ShelfDeletionNotAllowedException
import net.thechance.dukan.service.exception.ShelfNameAlreadyTakenException
import net.thechance.dukan.service.exception.ShelfNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
import net.thechance.dukan.service.exception.ShelfNameNotChangedException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class DukanShelfService(
    private val dukanShelfRepository: DukanShelfRepository,
    private val dukanService: DukanService,
    private val dukanProductRepository: DukanProductRepository
) {
    fun createShelf(title: String, ownerId: UUID): DukanShelf {
        val dukan = dukanService.getDukanByOwnerId(ownerId)

        return dukanShelfRepository.save(
            DukanShelf(
                title = title,
                dukan = dukan
            )
        )
    }

    fun updateShelf(ownerId: UUID, shelfId: UUID, newTitle: String) {
        val dukan = dukanService.getDukanByOwnerId(ownerId)

        val shelf = dukanShelfRepository.findByIdAndDukanId(shelfId, dukan.id)
            ?: throw ShelfNotFoundException()

        if (shelf.title == newTitle) throw ShelfNameNotChangedException()
        val newShelf = shelf.copy(title = newTitle)
        dukanShelfRepository.save(newShelf)
    }


    fun deleteShelf(shelfId: UUID, ownerId: UUID) {
        val shelf = getShelfById(shelfId, ownerId)

        if (dukanProductRepository.existsByShelfId(shelfId)) {
            throw ShelfDeletionNotAllowedException()
        }

        dukanShelfRepository.delete(shelf)
    }

    fun getDukanShelvesByOwnerId(ownerId: UUID): List<DukanShelf> {

        val dukan = dukanService.getDukanByOwnerId(ownerId)

        return dukanShelfRepository.findAllByDukanId(dukan.id)
    }

    fun getShelfById(shelfId: UUID, ownerId: UUID): DukanShelf {
        val dukan = dukanService.getDukanByOwnerId(ownerId)
        return dukanShelfRepository.findByIdAndDukanId(shelfId, dukan.id)
            ?: throw ShelfNotFoundException()
    }

    fun getAllShelvesByDukanId(dukanId: UUID, pageable: Pageable): Page<DukanShelf> {
        return dukanShelfRepository.findAllByDukanId(dukanId, pageable)
    }
}