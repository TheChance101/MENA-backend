package net.thechance.identity.api.controller;

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
class DeepLinkConfigurationController {

	private val associationFileContent: String? by lazy {
		runCatching {
			ClassPathResource("apple-app-site-association")
				.inputStream.use { stream ->
					String(stream.readAllBytes(), StandardCharsets.UTF_8)
				}
		}.getOrNull()
	}

	@GetMapping(
		value = ["/apple-app-site-association"],
		produces = [MediaType.APPLICATION_JSON_VALUE]
	)
	fun appleAppSiteAssociation(): ResponseEntity<String> {
		return associationFileContent?.let { content ->
			ResponseEntity.ok(content)
		} ?: ResponseEntity.notFound().build()
	}
}