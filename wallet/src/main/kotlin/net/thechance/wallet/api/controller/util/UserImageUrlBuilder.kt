package net.thechance.wallet.api.controller.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class UserImageUrlBuilder(
    @Value("\${storage.mena.cdn-endpoint}") cdnEndpoint: String,
    @Value("\${identity.resources.profile-image-directory}") profileImageDirectory: String,
) {
    private val imageBaseUrl: String = "$cdnEndpoint$profileImageDirectory"

    fun buildImageUrl(imageKey: String?): String? {
        return imageKey?.let { "$imageBaseUrl/$it" }
    }
}