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


    @Query(
        """
        SELECT m FROM Mosque m
        WHERE (
            6371 * acos(
                cos(radians(:lat)) * cos(radians(m.latitude)) *
                cos(radians(m.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(m.latitude))
            )
        ) <= :radius
        """
    )
    fun findNearbyMosques(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("radius") radius: Double
    ): List<Mosque>
}
