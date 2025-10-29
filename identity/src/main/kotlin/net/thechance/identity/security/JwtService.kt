package net.thechance.identity.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import net.thechance.identity.entity.User
import net.thechance.identity.entity.AdminUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Service
class JwtService(
    @param:Value("\${jwt.secret-key}") val secret: String
) {
    private val accessExpiration = Duration.ofHours(1)

    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
    }

    fun generateToken(user: User): String =
        Jwts.builder()
            .setSubject(user.id.toString())
            .setIssuedAt(Date())
            .setExpiration(Date.from(Instant.now().plus(accessExpiration)))
            .signWith(secretKey)
            .compact()

    fun generateToken(adminUser: AdminUser): String =
        Jwts.builder()
            .setSubject(adminUser.id.toString())
            .setIssuedAt(Date())
            .setExpiration(Date.from(Instant.now().plus(accessExpiration)))
            .claim(ADMIN_CLAIM, true)
            .signWith(secretKey)
            .compact()

    fun extractUserId(token: String): UUID {
        val subject = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject

        return UUID.fromString(subject)
    }

    fun isAdminToken(token: String): Boolean {
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body

        return when (val adminClaim = claims[ADMIN_CLAIM]) { //TODO: check if always Boolean
            is Boolean -> adminClaim
            is String -> adminClaim.toBoolean()
            else -> false
        }
    }

    companion object {
        const val ADMIN_CLAIM = "admin"
    }
}