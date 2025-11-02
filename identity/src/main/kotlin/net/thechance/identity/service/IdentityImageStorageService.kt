package net.thechance.identity.service

import net.thechance.identity.exception.InvalidImageException
import net.thechance.identity.exception.UnknownErrorException
import net.thechance.identity.security.config.IdentityStorageProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime

@Service
@EnableConfigurationProperties(IdentityStorageProperties::class)
class IdentityImageStorageService(
    private val menaS3Client: S3Client,
    private val identityStorageProperties: IdentityStorageProperties
) {
    fun uploadImage(
        file: MultipartFile,
        fileName: String,
        folderName: String
    ): String {
        val mimeType = file.contentType ?: throw InvalidImageException("null")
        val extension = allowedMimeTypes[mimeType] ?: throw InvalidImageException(mimeType)
        try {
            val fileName = "${fileName}.$extension"
            val randomParameter = LocalDateTime.now().toString()
            val key = "$folderName$fileName"
            val putReq = createObjectRequest(key, mimeType)
            menaS3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            val imageUri = "$fileName?time=$randomParameter"
            return imageUri
        } catch (e: Exception) {
            throw UnknownErrorException(e.message ?: "Unknown error occurred")
        }
    }

    fun deleteImage(imageUrl: String) {
        try {
            val deleteRequest = deleteObjectRequest(imageUrl)
            menaS3Client.deleteObject(deleteRequest)
        } catch (e: Exception) {
            throw UnknownErrorException(e.message ?: "Unknown error occurred")
        }
    }

    private fun createObjectRequest(key: String, contentType: String): PutObjectRequest? {
        return PutObjectRequest.builder()
            .bucket(identityStorageProperties.bucket)
            .key(key)
            .contentType(contentType)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build()
    }

    private fun deleteObjectRequest(key: String): DeleteObjectRequest {
        return DeleteObjectRequest.builder()
            .bucket(identityStorageProperties.bucket)
            .key(key)
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