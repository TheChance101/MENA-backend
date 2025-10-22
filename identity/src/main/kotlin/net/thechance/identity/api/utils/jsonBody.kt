package net.thechance.identity.api.utils

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity

fun <T> ResponseEntity.BodyBuilder.jsonBody(key: String, value: T): ResponseEntity<String> {
    return contentType(APPLICATION_JSON)
        .body(mapToJson(key, value))
}

private fun <T> mapToJson(key: String, value: T): String {
    return "{\"$key\": \"$value\"}"
}