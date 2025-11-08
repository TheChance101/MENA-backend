package net.thechance.trends.service

import net.thechance.trends.exception.*
import net.thechance.trends.service.config.TrendsExpirationProperties
import net.thechance.trends.service.config.TrendsStorageProperties
import org.springframework.beans.factory.annotation.Value
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


@Service
@EnableConfigurationProperties(TrendsStorageProperties::class, TrendsExpirationProperties::class)
class FileStorageService(
    private val trendsS3Client: S3Client,
    private val trendsS3Presigner: S3Presigner,
    private val trendsStorageProperties: TrendsStorageProperties,
    private val trendsExpirationProperties: TrendsExpirationProperties,
    @param:Value("\${trends-access.secret-value}") private val accessKey: String
) {
    fun uploadVideo(
        file: MultipartFile,
    ): String {
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
            throw ThumbnailUploadFailedException()
        }
    }

    fun deleteFile(fileUrl: String): Boolean {
        val prefix = trendsStorageProperties.cdnEndpoint
        if (!fileUrl.startsWith(prefix)) {
            throw InvalidTrendInputException()
        }

        val key = fileUrl.removePrefix(prefix)

        val deleteRequest = DeleteObjectRequest.builder().bucket(trendsStorageProperties.bucket).key(key).build()

        val response = trendsS3Client.deleteObject(deleteRequest)

        return response.sdkHttpResponse().isSuccessful
    }

    fun generatePresignedUrl(
        key: String,
        expirationMinutes: Long = trendsExpirationProperties.videoUrlMinutes
    ): String {
        runCatching {
            val getObjectRequest = getObjectRequest(key)
            val presignRequest = getObjectPresignRequest(expirationMinutes, getObjectRequest)
            val presignedRequest = trendsS3Presigner.presignGetObject(presignRequest)

            return presignedRequest.url().toString()

        }.getOrElse {
            throw TrendUrlSigningException("Failed to generate presigned URL for key: $key")
        }
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
            .overrideConfiguration { configuration ->
                configuration.putHeader("X-ACCESS-KEY", accessKey)
            }
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
    }
}