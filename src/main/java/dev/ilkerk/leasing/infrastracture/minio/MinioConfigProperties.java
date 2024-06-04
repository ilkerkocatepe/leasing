package dev.ilkerk.leasing.infrastracture.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioConfigProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
    private String endpoint;
    private int multipartMinPartSize;
}
