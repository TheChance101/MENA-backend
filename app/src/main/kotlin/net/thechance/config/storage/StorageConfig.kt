package net.thechance.config.storage

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
@EnableConfigurationProperties(AllStorageProperties::class)
class StorageConfig(
    private val props: AllStorageProperties,
) {
    @Bean
    fun menaS3Client(menaCreds: StaticCredentialsProvider): S3Client =
        buildClient(props.mena.endpoint, menaCreds)

    @Bean
    fun dukanS3Client(dukanCreds: StaticCredentialsProvider): S3Client =
        buildClient(props.dukan.endpoint, dukanCreds)

    @Bean
    fun trendsS3Client(trendCreds: StaticCredentialsProvider): S3Client =
        buildClient(props.trends.endpoint, trendCreds)

    @Bean
    fun walletS3Client(walletCreds: StaticCredentialsProvider): S3Client =
        buildClient(props.wallet.endpoint, walletCreds)

    @Bean
    fun menaCreds(): StaticCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(props.mena.key, props.mena.secret))
    }

    @Bean
    fun dukanCreds(): StaticCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(props.dukan.key, props.dukan.secret))
    }

    @Bean
    fun trendCreds(): StaticCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(props.trends.key, props.trends.secret))
    }

    @Bean
    fun walletCreds(): StaticCredentialsProvider {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(props.wallet.key, props.wallet.secret))
    }

    private fun buildClient(
        endpoint: String,
        creds: StaticCredentialsProvider
    ): S3Client {
        return S3Client.builder()
            .region(Region.US_EAST_1) // The region is required by AWS SDK, but DigitalOcean Spaces ignores it
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(creds)
            .serviceConfiguration(pathStyleStorageConfiguration())
            .build()
    }

    @Bean
    fun pathStyleStorageConfiguration(): S3Configuration {
        return S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()
    }
}