package net.thechance.trends.service

import net.thechance.trends.exception.*
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime

@ConfigurationProperties(prefix = "storage.trends")
data class TrendsStorageProperties(
    val bucket: String,
    val cdnEndpoint: String
)

@Service
@EnableConfigurationProperties(TrendsStorageProperties::class)
class FileStorageService(
    private val trendsS3Client: S3Client,
    private val trendsStorageProperties: TrendsStorageProperties,
) {
    fun uploadVideo(
        file: MultipartFile,
        fileName: String,
        folderName: String,
    ): String {
        val mimeType = file.contentType ?: throw InvalidVideoException()
        val extension = allowedVideoMimeTypes[mimeType] ?: throw InvalidVideoException()
        runCatching {
            val newFileName = "${fileName}_${LocalDateTime.now()}.$extension"
            val key = "video/$folderName/$newFileName"
            val putReq = createObjectRequest(key, mimeType)
            trendsS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return "${trendsStorageProperties.cdnEndpoint}/${trendsStorageProperties.bucket}/$key"
        }.getOrElse {
            throw VideoUploadFailedException()
        }
    }

    fun uploadThumbnail(
        file: MultipartFile,
        fileName: String,
        folderName: String,
    ): String {
        val mimeType = file.contentType ?: throw InvalidThumbnailException()
        val extension = allowedImageMimeTypes[mimeType] ?: throw InvalidThumbnailException()
        runCatching {
            val newFileName = "${fileName}_${LocalDateTime.now()}.$extension"
            val key = "thumbnail/$folderName/$newFileName"
            val putReq = createObjectRequest(key, mimeType)
            trendsS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return "${trendsStorageProperties.cdnEndpoint}/${trendsStorageProperties.bucket}/$key"
        }.getOrElse {
            throw ThumbnailUploadFailedException()
        }
    }

    fun deleteVideo(videoUrl: String): Boolean {
        val prefix = trendsStorageProperties.cdnEndpoint
        if (!videoUrl.startsWith(prefix)) {
            throw InvalidTrendInputException()
        }

        val key = videoUrl.removePrefix(prefix)

        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(trendsStorageProperties.bucket)
            .key(key)
            .build()

        val response = trendsS3Client.deleteObject(deleteRequest)

        return response.sdkHttpResponse().isSuccessful
    }

    private fun createObjectRequest(key: String, contentType: String): PutObjectRequest? {
        return PutObjectRequest.builder()
            .bucket(trendsStorageProperties.bucket)
            .key(key)
            .contentType(contentType)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()
    }

    private companion object {
        val allowedVideoMimeTypes = mapOf(
            "video/mp4" to "mp4",
            "video/quicktime" to "mov",
            "video/x-matroska" to "mkv"
        )

        val allowedImageMimeTypes = mapOf(
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp"
        )
    }
}