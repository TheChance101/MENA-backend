package net.thechance.dukan.repository

import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.service.model.DukanWithFavorite
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DukanRepository : JpaRepository<Dukan, UUID> {
    fun existsByName(name: String): Boolean
    fun existsByOwnerId(ownerId: UUID): Boolean
    fun findByOwnerId(ownerId: UUID): Dukan?

    @Query(
        """
    SELECT DISTINCT d
    FROM Dukan d
    JOIN d.categories c
    WHERE d.status = net.thechance.dukan.entity.Dukan.Status.ACTIVATED
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
    fun findActivatedDukansWithProductsByCategory(
        categoryId: UUID,
        pageable: Pageable
    ): Page<Dukan>


    @Query(
        """
    SELECT DISTINCT d
    FROM Dukan d
    WHERE d.status = net.thechance.dukan.entity.Dukan.Status.ACTIVATED
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
    fun findAllActivatedWithShelvesAndProducts(pageable: Pageable): Page<Dukan>

    @Query(
        """
    SELECT new net.thechance.dukan.service.model.DukanWithFavorite(
        dukan,
        CASE WHEN favoriteDukan.id.dukanId IS NOT NULL THEN true ELSE false END
    )
    FROM Dukan dukan
    LEFT JOIN FavoriteDukan favoriteDukan
        ON favoriteDukan.id.dukanId = dukan.id AND favoriteDukan.id.userId = :userId
    WHERE dukan.status = net.thechance.dukan.entity.Dukan.Status.ACTIVATED
      AND EXISTS (
          SELECT 1 
          FROM DukanShelf dukanShelf
          WHERE dukanShelf.dukan = dukan
      )
      AND EXISTS (
          SELECT 1
          FROM DukanProduct dukanProduct
          WHERE dukanProduct.dukan = dukan
      )
    ORDER BY dukan.createdAt DESC
    """
    )
    fun findAllActivatedWithShelvesAndProducts(
        userId: UUID,
        pageable: Pageable
    ): Page<DukanWithFavorite>

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
                WHERE d.status = 'ACTIVATED'
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
                WHERE d.status = 'ACTIVATED'
                  AND EXISTS (SELECT 1 FROM dukan.dukan_shelves s WHERE s.dukan_id = d.id)
                  AND EXISTS (SELECT 1 FROM dukan.dukan_products p WHERE p.dukan_id = d.id)
            )
            SELECT COUNT(*) FROM filtered WHERE distance <= :range
            """,
        nativeQuery = true
    )
    fun findBestAroundActivatedDukans(
        @Param("lat") lat: Double,
        @Param("lng") lng: Double,
        @Param("range") range: Double,
        pageable: Pageable
    ): Page<Dukan>

    @Query(
        """
    SELECT new net.thechance.dukan.service.model.DukanWithFavorite(
        dukan,
        CASE WHEN favoriteDukan.id.dukanId IS NOT NULL THEN true ELSE false END
    )
    FROM Dukan dukan
    JOIN dukan.categories category
    LEFT JOIN FavoriteDukan favoriteDukan
        ON favoriteDukan.id.dukanId = dukan.id AND favoriteDukan.id.userId = :userId
    WHERE category.id = :categoryId
    """
    )
    fun findAllByCategoryWithFavorite(
        categoryId: UUID,
        userId: UUID,
        pageable: Pageable
    ): Page<DukanWithFavorite>

    @Query(
        """
    SELECT d FROM Dukan d
    WHERE 
        d.status = :status
        AND (
            LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(d.address) LIKE LOWER(CONCAT('%', :query, '%'))
        )
    """
    )
    fun findByNameOrAddressAndStatus(
        @Param("query") query: String,
        @Param("status") status: Dukan.Status,
        pageable: Pageable
    ): Page<Dukan>

    @Modifying
    @Query("UPDATE Dukan d SET d.status = :status WHERE d.id = :dukanId")
    fun updateStatus(
        @Param("dukanId") dukanId: UUID,
        @Param("status") status: Dukan.Status
    ): Int
}