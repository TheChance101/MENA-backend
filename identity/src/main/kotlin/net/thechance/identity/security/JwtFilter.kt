package net.thechance.identity.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.thechance.identity.security.handler.AuthErrorResponder
import net.thechance.identity.service.UserService
import net.thechance.identity.repository.AdminUserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtFilter(
    private val jwtService: JwtService,
    private val userService: UserService,
    private val adminUserRepository: AdminUserRepository,
    private val authErrorResponder: AuthErrorResponder
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")

            val token = authHeader?.takeIf { it.startsWith("Bearer ") }?.removePrefix("Bearer ")?.trim()

            if (token != null && SecurityContextHolder.getContext().authentication == null) {
                val userId = jwtService.extractUserId(token)

                validateAccessForToken(token, request, userId)

                val authentication = UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    emptyList()
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }

            filterChain.doFilter(request, response)
        } catch (_: ExpiredJwtException) {
            authErrorResponder.handleJwtExpired(response)
        } catch (_: MalformedJwtException) {
            authErrorResponder.handleInvalidToken(response)
        } catch (_: Exception) {
            authErrorResponder.handleGeneralAuthError(response)
        }
    }

    private fun validateAccessForToken(token: String, request: HttpServletRequest, userId: UUID) {
        if (jwtService.isAdminToken(token)) {
            if (!request.requestURI.contains("/admin")) throw IllegalStateException("Not authorized for user access")
            if (!adminUserRepository.existsById(userId)) throw IllegalStateException("Admin user not found")
        } else {
            if (request.requestURI.contains("/admin")) throw IllegalStateException("Not authorized for admin access")
            if (!userService.userExists(userId)) throw IllegalStateException("User not found")
        }
    }

    companion object {
        fun getUserId(): UUID = SecurityContextHolder.getContext().authentication.principal as UUID
    }
}