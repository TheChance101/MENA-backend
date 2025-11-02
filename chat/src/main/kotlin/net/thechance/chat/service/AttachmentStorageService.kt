package net.thechance.chat.service

import net.thechance.chat.service.exception.ImageUploadFailedException
import net.thechance.chat.service.exception.InvalidImageFormatException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime


@ConfigurationProperties(prefix = "storage.mena")
data class ChatStorageProperties(
    val bucket: String,
    val cdnEndpoint: String
)

@Service
@EnableConfigurationProperties(ChatStorageProperties::class)
class AttachmentStorageService(
    private val menaS3Client: S3Client,
    private val props: ChatStorageProperties,
) {
    fun uploadImage(
        file: MultipartFile,
        folderName: String,
    ): String {
        val mimeType = file.contentType ?: throw InvalidImageFormatException()
        val extension = allowedMimeTypes[mimeType] ?: throw InvalidImageFormatException()
        try {
            val finalFileName = "${LocalDateTime.now()}.$extension"
            val key = "images/$folderName/$finalFileName"
            val putReq = createObjectRequest(key, mimeType)
            menaS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return makeUrl(key)
        } catch (e: Exception) {
            throw ImageUploadFailedException("failed uploading image: ${e.message}")
        }
    }

    private fun makeUrl(key: String): String {
        val base = props.cdnEndpoint.trimEnd('/')
        val path = key.trimStart('/')
        return "$base/$path"
    }

    private fun createObjectRequest(key: String, contentType: String): PutObjectRequest? {
        return PutObjectRequest.builder()
            .bucket(props.bucket)
            .key(key)
            .contentType(contentType)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()
    }

    private companion object {
        val allowedMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/jpg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp",
        )
    }
}