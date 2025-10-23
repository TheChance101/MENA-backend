package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.api.exception.ShelfDeletionNotAllowedException
import net.thechance.dukan.api.exception.ShelfNameAlreadyTakenException
import net.thechance.dukan.api.exception.ShelfNotFoundException
import net.thechance.dukan.repository.DukanProductRepository
import net.thechance.dukan.repository.DukanShelfRepository
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