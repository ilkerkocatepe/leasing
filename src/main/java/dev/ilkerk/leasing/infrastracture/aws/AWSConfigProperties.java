package dev.ilkerk.leasing.infrastracture.aws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws")
public class AWSConfigProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
    private String endpoint;
    private int multipartMinPartSize;
}
