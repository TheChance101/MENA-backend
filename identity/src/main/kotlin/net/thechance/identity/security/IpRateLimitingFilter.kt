package net.thechance.identity.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.thechance.identity.security.config.RateLimitProperties
import net.thechance.identity.service.RateLimitManagerService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class IpRateLimitingFilter(
    private val rateLimitProperties: RateLimitProperties,
    private val rateLimitManagerService: RateLimitManagerService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        val requestUri = request.requestURI
        val clientIp = request.remoteAddr

        val canRateLimited = rateLimitProperties.endpoints.containsKey(requestUri)

        if (authHeader == null && canRateLimited) {
            if (!rateLimitManagerService.isRequestAllowed(clientIp, requestUri)) {
                response.status = HttpStatus.TOO_MANY_REQUESTS.value()
                response.contentType = "text/plain"
                response.writer.write("Too many requests from this IP for '$requestUri'. Please try again later.")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}