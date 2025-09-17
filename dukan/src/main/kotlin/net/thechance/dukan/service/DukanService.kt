package net.thechance.dukan.service

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.exception.DukanNotFoundException
import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
) {
    fun isDukanNameAvailable(name: String): Boolean = dukanRepository.existsByName(name).not()
    fun getDukanByOwnerId(ownerId: String): Dukan {
       return  dukanRepository.findByOwnerId(
            ownerId.let { UUID.fromString(it) }
        ) ?: throw DukanNotFoundException()
    }
}