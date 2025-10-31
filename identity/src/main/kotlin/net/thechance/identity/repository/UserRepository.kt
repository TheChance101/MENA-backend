package net.thechance.identity.repository

import net.thechance.identity.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserRepository: JpaRepository<User, UUID> {
    fun findByPhoneNumber(phoneNumber: String): User?

    @Query("""
    SELECT u FROM User u
    WHERE 
        LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
        OR u.phoneNumber LIKE CONCAT('%', :query, '%')
""")
    fun findByFullNameOrPhoneNumber(
        @Param("query") query: String,
        pageable: Pageable
    ): Page<User>
}