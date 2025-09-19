package net.thechance.dukan.service

import net.thechance.dukan.exception.ImageUploadingFailedException
import net.thechance.dukan.exception.InvalidPictureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.time.LocalDateTime

@Service
class ImageStorageService(
    private val s3Client: S3Client,
    @param:Value("\${storage.bucket}") val bucket: String,
    @param:Value("\${storage.cdn-endpoint}") val cdnEndpoint: String
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
            s3Client.putObject(putReq, RequestBody.fromBytes(file.bytes))
            return "${cdnEndpoint}/$key"
        } catch (_: Exception) {
            throw ImageUploadingFailedException()
        }
    }

    private fun createObjectRequest(key: String, contentType: String): PutObjectRequest? {
        return PutObjectRequest.builder()
            .bucket(bucket)
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