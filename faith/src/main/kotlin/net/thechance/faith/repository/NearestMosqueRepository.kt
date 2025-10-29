package net.thechance.faith.repository

import net.thechance.faith.entity.Mosque
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NearestMosqueRepository: JpaRepository<Mosque, UUID> {



}
