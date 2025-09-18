package net.thechance.identity.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.thechance.app.exception.ApiErrorResponse
import net.thechance.identity.service.UserService
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
    private val objectMapper: ObjectMapper
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
                if (!userService.userExists(userId)) throw IllegalStateException("User not found")
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
            handleJwtExpired(response)
        } catch (_: MalformedJwtException) {
            handleInvalidToken(response)
        } catch (_: Exception) {
            handleGeneralAuthError(response)
        }
    }

    private fun handleJwtExpired(
        response: HttpServletResponse
    ) {
        val apiError = ApiErrorResponse(
            status = 1001,
            message = "JWT token has expired. Please login again."
        )
        sendErrorResponse(response, apiError)
    }

    private fun handleInvalidToken(
        response: HttpServletResponse
    ) {
        val apiError = ApiErrorResponse(
            status = 1002,
            message = "Invalid JWT token format"
        )
        sendErrorResponse(response, apiError)
    }

    fun handleGeneralAuthError(
        response: HttpServletResponse
    ) {
        val apiError = ApiErrorResponse(
            status = HttpServletResponse.SC_UNAUTHORIZED,
            message = "Access Denied"
        )
        sendErrorResponse(response, apiError)
    }

    private fun sendErrorResponse(
        response: HttpServletResponse,
        apiError: ApiErrorResponse
    ) {
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        response.writer.write(objectMapper.writeValueAsString(apiError))
    }

    companion object {
        fun getUserId(): UUID = SecurityContextHolder.getContext().authentication.principal as UUID
    }
}