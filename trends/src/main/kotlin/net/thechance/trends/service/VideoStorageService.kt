package net.thechance.trends.service

import net.thechance.trends.exception.InvalidTrendInputException
import net.thechance.trends.exception.InvalidVideoException
import net.thechance.trends.exception.VideoUploadFailedException
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
class VideoStorageService(
    private val trendsS3Client: S3Client,
    private val trendsStorageProperties: TrendsStorageProperties,
) {
    fun uploadVideo(
        file: MultipartFile,
        fileName: String,
        folderName: String,
    ): String {
        val mimeType = file.contentType ?: throw InvalidVideoException()
        val extension = allowedMimeTypes[mimeType] ?: throw InvalidVideoException()
        runCatching {
            val fileName = "${fileName}_${LocalDateTime.now()}.$extension"
            val key = "video/$folderName/$fileName"
            val putReq = createObjectRequest(key, mimeType)
            trendsS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return "${trendsStorageProperties.cdnEndpoint}/${trendsStorageProperties.bucket}/$key"
        }.getOrElse {
            throw VideoUploadFailedException()
        }
    }

    private fun createObjectRequest(key: String, contentType: String): PutObjectRequest? {
        return PutObjectRequest.builder()
            .bucket(trendsStorageProperties.bucket)
            .key(key)
            .contentType(contentType)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()
    }

    fun deleteVideo(videoUrl: String) {

        val prefix = "${trendsStorageProperties.cdnEndpoint}/${trendsStorageProperties.bucket}/"
        if (!videoUrl.startsWith(prefix)) {
            throw InvalidTrendInputException()
        }

        val key = videoUrl.removePrefix(prefix)

        val deleteRequest = DeleteObjectRequest.builder()
            .bucket(trendsStorageProperties.bucket)
            .key(key)
            .build()

        trendsS3Client.deleteObject(deleteRequest)
    }

    private companion object {
        val allowedMimeTypes = mapOf(
            "video/mp4" to "mp4",
            "video/quicktime" to "mov",
            "video/x-matroska" to "mkv"
        )
    }
}