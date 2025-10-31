package net.thechance.faith.repository.reciters

import net.thechance.faith.entity.Reciter
import org.springframework.data.jpa.repository.JpaRepository

interface RecitersRepository : JpaRepository<Reciter, Int>
