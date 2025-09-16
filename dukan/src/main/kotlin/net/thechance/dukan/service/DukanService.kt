package net.thechance.dukan.service

import net.thechance.dukan.api.dto.DukanStyleResponse
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.mapper.toDukanStyleResponse
import net.thechance.dukan.repository.DukanCategoryRepository
import net.thechance.dukan.repository.DukanColorRepository
import net.thechance.dukan.repository.DukanRepository
import org.springframework.stereotype.Service

@Service
class DukanService(
    private val dukanRepository: DukanRepository,
    private val dukanColorRepository: DukanColorRepository,
    private val dukanCategoryRepository: DukanCategoryRepository
) {
    fun getAllStyles(): DukanStyleResponse = Dukan.Style.entries.toDukanStyleResponse()
}