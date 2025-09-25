package net.thechance.dukan.service

import net.thechance.dukan.entity.DukanShelf
import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.exception.ShelfNameAlreadyTakenException
import net.thechance.dukan.repository.DukanRepository
import net.thechance.dukan.repository.DukanShelfRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DukanShelfService(
    private val dukanShelfRepository: DukanShelfRepository,
    private val dukanRepository: DukanRepository,
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
}