package net.thechance.chat.service

import net.thechance.chat.service.exception.DeleteImagesFolderException
import net.thechance.chat.service.exception.ImageUploadFailedException
import net.thechance.chat.service.exception.InvalidImageFormatException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.Delete
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
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
        fileName: String,
        folderName: String,
    ): String {
        val mimeType = file.contentType ?: throw InvalidImageFormatException()
        val extension = allowedMimeTypes[mimeType] ?: throw InvalidImageFormatException()
        try {
            val finalFileName = "${fileName}_${LocalDateTime.now()}.$extension"
            val key = "$CHAT_ATTACHMENTS_PATH/$folderName/$finalFileName"
            val putReq = createObjectRequest(key, mimeType)
            menaS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return "${props.cdnEndpoint}/$key"
        } catch (e: Exception) {
            throw ImageUploadFailedException("failed uploading image: ${e.message}")
        }
    }

    fun deleteFolder(folderName: String) {
        try {
            val folderPath = "$CHAT_ATTACHMENTS_PATH/$folderName"
            var continuationToken: String? = null

            do {
                val listResponse = menaS3Client.listObjectsV2 { builder ->
                    builder.bucket(props.bucket)
                        .prefix(folderPath)
                        .continuationToken(continuationToken)
                }

                val keys = listResponse.contents().map {
                    ObjectIdentifier.builder().key(it.key()).build()
                }

                if (keys.isNotEmpty()) {
                    val deleteRequest = createDeleteRequest(keys)
                    val result = menaS3Client.deleteObjects(deleteRequest)
                    handlePartialDeleteFailure(result)
                }

                continuationToken = listResponse.nextContinuationToken()
            } while (continuationToken != null)
        } catch (e: Exception) {
            throw DeleteImagesFolderException("error clean up image: ${e.message}")
        }


    }

    private fun createDeleteRequest(keys: List<ObjectIdentifier>): DeleteObjectsRequest{
        return DeleteObjectsRequest.builder()
            .bucket(props.bucket)
            .delete(Delete.builder().objects(keys).build())
            .build()
    }
    private fun handlePartialDeleteFailure(result: DeleteObjectsResponse) {
        var attempts = 0
        while (result.errors().isNotEmpty() && attempts < MAX_RETRIES ){
            val failedKeys = result.errors().map {
                ObjectIdentifier.builder().key(it.key()).build()
            }

            println("Retrying failed deletions: ${failedKeys.size} keys")
            val retryRequest = DeleteObjectsRequest.builder()
                .bucket(props.bucket)
                .delete(Delete.builder().objects(failedKeys).build())
                .build()
            menaS3Client.deleteObjects(retryRequest)
            attempts++
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
        const val MAX_RETRIES = 3
        const val CHAT_ATTACHMENTS_PATH = "images/chat_attachments"
    }
}