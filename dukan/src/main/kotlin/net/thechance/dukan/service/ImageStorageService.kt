package net.thechance.dukan.service

import net.thechance.dukan.exception.ImageUploadingFailedException
import net.thechance.dukan.exception.InvalidPictureException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime

@ConfigurationProperties(prefix = "storage.dukan")
data class DukanStorageProperties(
    val bucket: String,
    val cdnEndpoint: String
)

@Service
@EnableConfigurationProperties(DukanStorageProperties::class)
class ImageStorageService(
    private val dukanS3Client: S3Client,
    private val props: DukanStorageProperties,
) {
    fun uploadImage(
        file: MultipartFile,
        fileName: String,
        folderName: String,
    ): String {
        val mimeType = file.contentType ?: throw InvalidPictureException()
        val extension = allowedMimeTypes[mimeType] ?: throw InvalidPictureException()
        try {
            val fileName = "${fileName}_${LocalDateTime.now()}.$extension"
            val key = "images/$folderName/$fileName"
            val putReq = createObjectRequest(key, mimeType)
            dukanS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return "${props.cdnEndpoint}/$key"
        } catch (_: Exception) {
            throw ImageUploadingFailedException()
        }
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