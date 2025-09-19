package net.thechance.identity.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import identity.security.handler.ApiErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper = ObjectMapper()
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val apiError = ApiErrorResponse(
            status = HttpServletResponse.SC_UNAUTHORIZED,
            message = "Authentication required"
        )

        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(objectMapper.writeValueAsString(apiError))
    }
}