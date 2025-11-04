package net.thechance.faith.repository

import net.thechance.faith.entity.Mosque
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface MosqueRepository : JpaRepository<Mosque, UUID> {

    @Query(
        """
        SELECT m FROM Mosque m
        WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """
    )
    fun searchMosquesByName(
        @Param("keyword") keyword: String,
        pageable: Pageable
    ): Page<Mosque>
}
