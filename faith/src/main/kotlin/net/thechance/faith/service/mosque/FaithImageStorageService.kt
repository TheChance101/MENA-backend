package net.thechance.faith.service.mosque

import net.thechance.faith.exception.ImageUploadFailedException
import net.thechance.faith.exception.InvalidImageFormatException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@ConfigurationProperties(prefix = "storage.faith")
data class FaithStorageProperties(
    val bucket: String,
    val cdnEndpoint: String
)


@Service("mosqueImageStorageService")
@EnableConfigurationProperties(FaithStorageProperties::class)
class FaithImageStorageService(
    private val s3Client: S3Client,
    private val props: FaithStorageProperties,
) {

    fun uploadImage(
        file: MultipartFile,
        fileName: String,
        folderName: String,
    ): String {
        val mimeType = file.contentType ?: throw InvalidImageFormatException()
        val extension = allowedMimeTypes[mimeType] ?: throw InvalidImageFormatException()

        val safeFileName = generateSafeFileName(fileName, extension)
        val key = "images/$folderName/$safeFileName"

        return try {
            val putRequest = PutObjectRequest.builder()
                .bucket(props.bucket)
                .key(key)
                .contentType(mimeType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build()

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.bytes))


            "${props.cdnEndpoint}/$key"
        } catch (e: Exception) {
            throw ImageUploadFailedException("Failed to upload image: ${e.message}")
        }
    }


    private fun generateSafeFileName(baseName: String, extension: String): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val randomSuffix = UUID.randomUUID().toString().take(8)
        val cleanedName = baseName
            .replace("\\s+".toRegex(), "_")
            .replace("[^A-Za-z0-9_\\-]".toRegex(), "")
        return "${cleanedName}_${timestamp}_$randomSuffix.$extension"
    }

    private companion object {
        val allowedMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/jpg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp"
        )
    }
}