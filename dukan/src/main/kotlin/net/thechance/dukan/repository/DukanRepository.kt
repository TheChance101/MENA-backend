package net.thechance.dukan.repository

import net.thechance.dukan.entity.Dukan
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import software.amazon.awssdk.services.s3.endpoints.internal.Value.Str
import java.util.UUID

@Repository
interface DukanRepository : JpaRepository<Dukan, UUID> {
    fun existsByName(name: String): Boolean
    fun existsByOwnerId(ownerId: UUID): Boolean
    fun findByOwnerId(ownerId: UUID): Dukan
    @Query(
        """
    SELECT DISTINCT d
    FROM Dukan d
    JOIN d.categories c
    WHERE d.status = net.thechance.dukan.entity.Dukan.Status.APPROVED
      AND c.id = :categoryId
      AND EXISTS (
          SELECT 1 
          FROM DukanShelf s
          WHERE s.dukan = d
      )
      AND EXISTS (
          SELECT 1
          FROM DukanProduct p
          WHERE p.dukan = d
      )
    ORDER BY d.createdAt DESC
    """
    )
    fun findApprovedDukansWithProductsByCategory(
        categoryId: UUID,
        pageable: Pageable
    ): Page<Dukan>
    @Query(
        """
    SELECT DISTINCT d
    FROM Dukan d
    WHERE d.status = net.thechance.dukan.entity.Dukan.Status.APPROVED
    AND EXISTS (
        SELECT 1 
        FROM DukanShelf s
        WHERE s.dukan = d
    )
    AND EXISTS (
        SELECT 1
        FROM DukanProduct p
        WHERE p.dukan = d
    )
    ORDER BY d.createdAt DESC
    """
    )
    fun findAllApprovedWithShelvesAndProducts(pageable: Pageable): Page<Dukan>


    @Query(
        value = """
            WITH filtered AS (
                SELECT d.*,
                    (6371000 * acos(
                        cos(radians(:lat)) * cos(radians(d.latitude)) *
                        cos(radians(d.longitude) - radians(:lng)) +
                        sin(radians(:lat)) * sin(radians(d.latitude))
                    )) AS distance
                FROM dukan.dukans d
                WHERE d.status = 'APPROVED'
                  AND EXISTS (SELECT 1 FROM dukan.dukan_shelves s WHERE s.dukan_id = d.id)
                  AND EXISTS (SELECT 1 FROM dukan.dukan_products p WHERE p.dukan_id = d.id)
            )
            SELECT * FROM filtered
            WHERE distance <= :range
            ORDER BY distance ASC
            """,
        countQuery = """
            WITH filtered AS (
                SELECT d.id,
                    (6371000 * acos(
                        cos(radians(:lat)) * cos(radians(d.latitude)) *
                        cos(radians(d.longitude) - radians(:lng)) +
                        sin(radians(:lat)) * sin(radians(d.latitude))
                    )) AS distance
                FROM dukan.dukans d
                WHERE d.status = 'APPROVED'
                  AND EXISTS (SELECT 1 FROM dukan.dukan_shelves s WHERE s.dukan_id = d.id)
                  AND EXISTS (SELECT 1 FROM dukan.dukan_products p WHERE p.dukan_id = d.id)
            )
            SELECT COUNT(*) FROM filtered WHERE distance <= :range
            """,
        nativeQuery = true
    )
    fun findBestAroundApprovedDukans(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("range") range: Double,
        pageable: Pageable
    ): Page<Dukan>

}