package net.thechance.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@ConfigurationProperties(prefix = "storage")
data class StorageProperties(
    val key: String,
    val secret: String,
    val endpoint: String,
)

@Configuration
@EnableConfigurationProperties(StorageProperties::class)
class StorageConfig(
    private val properties: StorageProperties,
) {

    @Bean
    fun s3Client(
        storageCredentialsProvider: StaticCredentialsProvider,
        storageConfiguration: S3Configuration,
    ): S3Client {
        return S3Client.builder()
            .region(Region.US_EAST_1) // The region is required by AWS SDK, but DigitalOcean Spaces ignores it
            .credentialsProvider(storageCredentialsProvider)
            .endpointOverride(URI.create(properties.endpoint))
            .serviceConfiguration(storageConfiguration)
            .build()
    }

    @Bean
    fun storageCredentialsProvider(): StaticCredentialsProvider {
        val awsBasicCredentials = AwsBasicCredentials.create(properties.key, properties.secret)
        return StaticCredentialsProvider.create(awsBasicCredentials)
    }

    @Bean
    fun pathStyleStorageConfiguration(): S3Configuration {
        return S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
    }
}