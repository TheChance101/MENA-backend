package net.thechance.faith.repository

import net.thechance.faith.entity.Mosque
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MosqueRepository: JpaRepository<Mosque, UUID> {

}