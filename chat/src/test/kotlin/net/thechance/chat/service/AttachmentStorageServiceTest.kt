package net.thechance.chat.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.thechance.chat.service.exception.ImageUploadFailedException
import net.thechance.chat.service.exception.InvalidImageFormatException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest

class AttachmentStorageServiceTest {

    private lateinit var menaS3Client: S3Client
    private lateinit var props: ChatStorageProperties
    private lateinit var service: AttachmentStorageService

    @BeforeEach
    fun setUp() {
        menaS3Client = mockk(relaxed = true)
        props = ChatStorageProperties(
            bucket = "test-bucket",
            cdnEndpoint = "https://cdn.test.com"
        )
        service = AttachmentStorageService(menaS3Client, props)
    }

    @Test
    fun `uploadImage should upload image successfully and return cdn url`() {
        val file = mockk<MultipartFile>()
        val fileBytes = "test".toByteArray()
        val fileName = "message123"
        val folderName = "chat_attachments"

        every { file.contentType } returns "image/jpeg"
        every { file.bytes } returns fileBytes
        every { file.originalFilename } returns "photo.jpg"
        every {
            menaS3Client.putObject(any<PutObjectRequest>(), any<RequestBody>())
        } returns mockk()

        val result = service.uploadImage(file, fileName, folderName)

        assertThat(result).startsWith("${props.cdnEndpoint}/images/$folderName/")
        assertThat(result).endsWith(".jpg")

        verify {
            menaS3Client.putObject(
                withArg<PutObjectRequest> {
                    assertThat(it.bucket()).isEqualTo(props.bucket)
                    assertThat(it.acl()).isEqualTo(ObjectCannedACL.PUBLIC_READ)
                    assertThat(it.key()).contains("images/$folderName/")
                },
                any<RequestBody>()
            )
        }
    }


    @Test
    fun `uploadImage should throw InvalidImageFormatException when mime type is null`() {
        val file = mockk<MultipartFile>()
        every { file.contentType } returns null

        assertThrows<InvalidImageFormatException> {
            service.uploadImage(file, "file1", "chat_attachments")
        }
    }


    @Test
    fun `uploadImage should throw InvalidImageFormatException when mime type is not supported`() {
        val file = mockk<MultipartFile>()
        every { file.contentType } returns "application/pdf"

        assertThrows<InvalidImageFormatException> {
            service.uploadImage(file, "file1", "chat_attachments")
        }
    }

    @Test
    fun `uploadImage should throw ImageUploadFailedException when putObject fails`() {
        val file = mockk<MultipartFile>()
        val fileBytes = "data".toByteArray()

        every { file.contentType } returns "image/png"
        every { file.bytes } returns fileBytes
        every { file.originalFilename } returns "image.png"

        every {
            menaS3Client.putObject(any<PutObjectRequest>(), any<RequestBody>())
        } throws RuntimeException("S3 failed")

        assertThrows<ImageUploadFailedException> {
            service.uploadImage(file, "file2", "chat_attachments")
        }
    }
}