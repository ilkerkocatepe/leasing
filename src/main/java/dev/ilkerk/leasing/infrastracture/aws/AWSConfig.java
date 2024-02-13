package dev.ilkerk.leasing.infrastracture.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.utils.StringUtils;

import java.net.URI;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AWSConfig {
    private final AWSConfigProperties awsConfigProperties;

    @Bean
    public S3AsyncClient s3AsyncClient(AWSConfigProperties awsConfigProperties) {
        log.info("Creating S3AsyncClient: " + awsConfigProperties.toString());
        return S3AsyncClient.builder()
                .httpClient(sdkAsyncHttpClient())
                .region(Region.of(awsConfigProperties.getRegion()))
                .credentialsProvider(awsCredentialsProvider())
                .endpointOverride(URI.create(awsConfigProperties.getEndpoint()))
                .forcePathStyle(true)
                .serviceConfiguration(s3Configuration()).build();
    }

    private SdkAsyncHttpClient sdkAsyncHttpClient() {
        return NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();
    }

    private S3Configuration s3Configuration() {
        return S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();
    }
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        if (StringUtils.isBlank(awsConfigProperties.getAccessKey())) {
            return DefaultCredentialsProvider.create();
        } else {
            return () -> AwsBasicCredentials.create(
                    awsConfigProperties.getAccessKey(),
                    awsConfigProperties.getSecretKey());
        }
    }
}
