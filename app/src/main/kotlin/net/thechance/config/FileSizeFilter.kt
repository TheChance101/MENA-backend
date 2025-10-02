package net.thechance.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class FileSizeFilter(
    private val fileSizeProperties: FileSizeProperties
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.contentType?.startsWith("multipart/form-data") == true) {
            val contentLength = request.contentLengthLong
            val maxSize = getMaxSizeForPath(request.requestURI)

            if (contentLength > maxSize) {
                response.status = HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE
                response.contentType = "application/json"
                response.characterEncoding = "UTF-8"
                response.writer.write(
                    """{"status": 413, "message": "File size exceeds ${maxSize / (1024 * 1024)}MB limit"}"""
                )
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getMaxSizeForPath(path: String): Long {
        val module = path.trim('/').split('/').firstOrNull() ?: ""
        val maxSizeMB = fileSizeProperties.getMaxSize(module)
        return maxSizeMB * 1024L * 1024L
    }
}