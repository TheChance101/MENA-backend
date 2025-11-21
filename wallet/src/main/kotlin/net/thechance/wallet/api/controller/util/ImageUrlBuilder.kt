package net.thechance.wallet.api.controller.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ImageUrlBuilder(
    @Value("\${storage.mena.cdn-endpoint}") cdnEndpoint: String,
    @Value("\${identity.resources.profile-image-directory}") profileImageDirectory: String,
) {
    private val userImageBaseUrl: String = "$cdnEndpoint$profileImageDirectory"

    fun buildUserImageUrl(imageKey: String?): String? {
        return imageKey?.let { "$userImageBaseUrl/$it" }
    }
}