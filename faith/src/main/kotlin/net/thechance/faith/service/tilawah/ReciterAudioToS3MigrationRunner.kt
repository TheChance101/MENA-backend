package net.thechance.faith.service.tilawah

import net.thechance.faith.repository.RecitersRepository
import net.thechance.faith.service.config.FaithStorageProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.File
import java.net.URL
import java.time.LocalDateTime

@Component
class ReciterAudioToS3MigrationRunner(
    private val recitersRepo: RecitersRepository,
    private val props: FaithStorageProperties,
    private val menaS3Client: S3Client
) : ApplicationRunner {

    private val markerFile = File("migration_done.marker")

    override fun run(args: ApplicationArguments?) {
        if (markerFile.exists()) return

        try {
            recitersRepo.findAll().forEach { reciter ->
                val oldBase = reciter.serverUrl.trimEnd('/')
                val folderName = reciter.name.replace(" ", "_")

                (1..114).forEach { surah ->
                    val fileName = surah.toString().padStart(3, '0') + ".zip"
                    val oldUrl = "$oldBase/zips/$fileName"
                    val newKey = "audio/$folderName/$fileName"

                    try {
                        val connection = URL(oldUrl).openConnection()
                        connection.connectTimeout = 30000
                        connection.readTimeout = 300000
                        val contentLength = connection.contentLengthLong

                        if (contentLength <= 0) return@forEach

                        val putRequest = PutObjectRequest.builder()
                            .bucket(props.bucket)
                            .key(newKey)
                            .contentType("application/zip")
                            .contentLength(contentLength)
                            .acl(ObjectCannedACL.PUBLIC_READ)
                            .build()

                        connection.getInputStream().use { inputStream ->
                            menaS3Client.putObject(
                                putRequest,
                                RequestBody.fromInputStream(inputStream, contentLength)
                            )
                        }
                    } catch (_: Exception) {
                    }
                }

                reciter.serverUrl = "${props.cdnEndpoint}/audio/$folderName/"
                recitersRepo.save(reciter)
            }

            markerFile.writeText(
                """
                Reciters audio migration to S3 completed successfully.
                Date: ${LocalDateTime.now()}
                This script will not run again unless this file is deleted.
                """.trimIndent()
            )
        } catch (_: Exception) {
        }
    }
}