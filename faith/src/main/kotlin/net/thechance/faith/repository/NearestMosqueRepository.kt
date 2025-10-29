package net.thechance.faith.repository

import net.thechance.faith.entity.Mosque
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NearestMosqueRepository: JpaRepository<Mosque, UUID> {

    @Query("""
    SELECT *, 
        (6371 * acos(
            cos(radians(:latitude)) * cos(radians(latitude)) *
            cos(radians(longitude) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(latitude))
        )) AS distance
    FROM faith.mosque
    WHERE 
        (6371 * acos(
            cos(radians(:latitude)) * cos(radians(latitude)) *
            cos(radians(longitude) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(latitude))
        )) <= :radiusKm
    ORDER BY distance
    """,
        nativeQuery = true)
    fun findNearestMosques(
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("radiusKm") radiusKm: Double
    ): List<Mosque>

    fun searchMosquesByName(name: String): List<Mosque>

}
