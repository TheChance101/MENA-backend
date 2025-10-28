package net.thechance.trends.service

import net.thechance.trends.exception.InvalidThumbnailException
import net.thechance.trends.exception.InvalidVideoException
import net.thechance.trends.exception.ThumbnailUploadFailedException
import net.thechance.trends.exception.VideoUploadFailedException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration
import java.time.LocalDateTime

@ConfigurationProperties(prefix = "storage.trends")
data class TrendsStorageProperties(
    val bucket: String,
    val cdnEndpoint: String,
    val key: String,
    val secret: String,
    val endpoint: String,
    val presignedUrlExpirationMinutes: Long = 5
)

@Service
@EnableConfigurationProperties(TrendsStorageProperties::class)
class FileStorageService(
    private val trendsS3Client: S3Client,
    private val trendsS3Presigner: S3Presigner,
    private val trendsStorageProperties: TrendsStorageProperties,
) {
    fun uploadVideo(file: MultipartFile): String {
        val mimeType = file.contentType ?: throw InvalidVideoException()
        val extension = allowedVideoMimeTypes[mimeType] ?: throw InvalidVideoException()

        val newFileName = "${LocalDateTime.now()}.$extension"
        val key = "video/$newFileName"

        val putRequest = createObjectRequest(key, mimeType)

        runCatching {
            file.inputStream.use { inputStream ->
                trendsS3Client.putObject(
                    putRequest, RequestBody.fromInputStream(inputStream, file.size)
                )
            }
            return key
        }.getOrElse {
            it.printStackTrace()
            throw VideoUploadFailedException()
        }
    }

    fun uploadImage(
        file: MultipartFile,
    ): String {
        val mimeType = file.contentType ?: throw InvalidThumbnailException()
        val extension = allowedImageMimeTypes[mimeType] ?: throw InvalidThumbnailException()
        runCatching {
            val newFileName = "${LocalDateTime.now()}.$extension"
            val key = "thumbnail/$newFileName"
            val putReq = createObjectRequest(key, mimeType)
            trendsS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return key
        }.getOrElse {
            it.printStackTrace()
            throw ThumbnailUploadFailedException()
        }
    }

    fun deleteFile(fileUrl: String): Boolean {
        val key = fileUrl.removePrefix(fileUrl)

        val deleteRequest = DeleteObjectRequest.builder().bucket(trendsStorageProperties.bucket).key(key).build()

        val response = trendsS3Client.deleteObject(deleteRequest)
        return response.sdkHttpResponse().isSuccessful
    }

    fun generatePresignedUrl(
        key: String,
        expirationMinutes: Long = VIDEO_URL_EXPIRATION_MINUTES
    ): String {
        try {
            val getObjectRequest = getObjectRequest(key)
            val presignRequest = getObjectPresignRequest(expirationMinutes, getObjectRequest)
            val presignedRequest = trendsS3Presigner.presignGetObject(presignRequest)

            val fullUrl = presignedRequest.url().toString()
            return fullUrl.substringAfter("://").substringAfter("/")

        } catch (ex: Exception) {
            ex.printStackTrace()
            throw RuntimeException("Failed to generate presigned URL for key: $key", ex)
        }
    }

    data class TrendUrls(
        val videoUrl: String,
        val thumbnailUrl: String?
    )

    fun generatePresignedUrlsForTrend(videoKey: String, thumbnailKey: String?): TrendUrls {
        val signedVideoUrl = generatePresignedUrl(videoKey, VIDEO_URL_EXPIRATION_MINUTES)
        val signedThumbnailUrl = thumbnailKey?.let {
            generatePresignedUrl(it, THUMBNAIL_URL_EXPIRATION_MINUTES)
        }
        return TrendUrls(videoUrl = signedVideoUrl , thumbnailUrl = signedThumbnailUrl)
    }

    private fun getObjectPresignRequest(
        expirationMinutes: Long,
        getObjectRequest: GetObjectRequest
    ): GetObjectPresignRequest {
        return GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(expirationMinutes))
            .getObjectRequest(getObjectRequest)
            .build()
    }

    private fun getObjectRequest(key: String): GetObjectRequest {
        return GetObjectRequest.builder()
            .bucket(trendsStorageProperties.bucket)
            .key(key)
            .build()
    }

    private fun createObjectRequest(key: String, contentType: String): PutObjectRequest? {
        return PutObjectRequest.builder().bucket(trendsStorageProperties.bucket).key(key).contentType(contentType)
            .acl(ObjectCannedACL.PRIVATE)
            .contentDisposition("inline")
            .build()
    }

    private companion object {
        val allowedVideoMimeTypes = mapOf(
            "video/mp4" to "mp4", "video/quicktime" to "mov", "video/x-matroska" to "mkv"
        )

        val allowedImageMimeTypes = mapOf(
            "image/jpeg" to "jpg", "image/jpg" to "jpg", "image/png" to "png", "image/webp" to "webp"
        )

        const val VIDEO_URL_EXPIRATION_MINUTES = 5L
        const val THUMBNAIL_URL_EXPIRATION_MINUTES = 5L
    }
}