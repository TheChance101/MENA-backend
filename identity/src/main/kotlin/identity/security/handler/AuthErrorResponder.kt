package net.thechance.identity.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import identity.security.handler.ApiErrorResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class AuthErrorResponder(
    private val objectMapper: ObjectMapper
) {

    fun handleJwtExpired(response: HttpServletResponse) {
        val apiError = ApiErrorResponse(
            status = 1001,
            message = "JWT token has expired. Please login again."
        )
        sendErrorResponse(response, apiError, HttpServletResponse.SC_UNAUTHORIZED)
    }

    fun handleInvalidToken(response: HttpServletResponse) {
        val apiError = ApiErrorResponse(
            status = 1002,
            message = "Invalid JWT token format"
        )
        sendErrorResponse(response, apiError, HttpServletResponse.SC_UNAUTHORIZED)
    }

    fun handleGeneralAuthError(response: HttpServletResponse) {
        val apiError = ApiErrorResponse(
            status = HttpServletResponse.SC_UNAUTHORIZED,
            message = "Access Denied"
        )
        sendErrorResponse(response, apiError, HttpServletResponse.SC_UNAUTHORIZED)
    }

    private fun sendErrorResponse(
        response: HttpServletResponse,
        apiError: ApiErrorResponse,
        status: Int
    ) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = status
        response.writer.write(objectMapper.writeValueAsString(apiError))
    }
}