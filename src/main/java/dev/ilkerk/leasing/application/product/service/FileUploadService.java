package dev.ilkerk.leasing.application.product.service;

import dev.ilkerk.leasing.domain.product.entity.FileDirectory;
import dev.ilkerk.leasing.infrastracture.aws.AWSConfigProperties;
import dev.ilkerk.leasing.infrastracture.aws.FileUtils;
import dev.ilkerk.leasing.infrastracture.aws.UploadResponse;
import dev.ilkerk.leasing.infrastracture.aws.UploadStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {
    private final S3AsyncClient s3AsyncClient;
    private final AWSConfigProperties awsConfigProperties;

    public Mono<UploadResponse> uploadObject(FilePart filePart, FileDirectory fileDirectory) {
        String filename = filePart.filename();

        String key = fileDirectory.getName() + "/" + filename;

        Map<String, String> metadata = Map.of("filename", filename);

        MediaType mediaType = ObjectUtils.defaultIfNull(filePart.headers().getContentType(), MediaType.APPLICATION_OCTET_STREAM);

        CompletableFuture<CreateMultipartUploadResponse> s3AsyncClientMultipartUpload = s3AsyncClient
                .createMultipartUpload(CreateMultipartUploadRequest.builder()
                        .contentType(mediaType.toString())
                        .metadata(metadata)
                        .key(key)
                        .bucket(awsConfigProperties.getBucketName())
                        .build());

        UploadStatus uploadStatus = new UploadStatus(Objects.requireNonNull(filePart.headers().getContentType()).toString(), key);

        return Mono.fromFuture(s3AsyncClientMultipartUpload)
                .flatMapMany(response -> {
                    try {
                        FileUtils.checkSdkResponse(response);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    uploadStatus.setUploadId(response.uploadId());
                    log.info("Upload object with ID={}", response.uploadId());
                    return filePart.content();
                })
                .bufferUntil(dataBuffer -> {
                    // Collect incoming values into multiple List buffers that will be emitted by the resulting Flux each time the given predicate returns true.
                    uploadStatus.addBuffered(dataBuffer.readableByteCount());

                    if (uploadStatus.getBuffered() >= awsConfigProperties.getMultipartMinPartSize()) {
                        log.info("BufferUntil - returning true, bufferedBytes={}, partCounter={}, uploadId={}",
                                uploadStatus.getBuffered(), uploadStatus.getPartCounter(), uploadStatus.getUploadId());

                        // reset buffer
                        uploadStatus.setBuffered(0);
                        return true;
                    }

                    return false;
                })
                .map(FileUtils::dataBufferToByteBuffer)
                .flatMap(byteBuffer -> uploadPartObject(uploadStatus, byteBuffer))
                .onBackpressureBuffer()
                .reduce(uploadStatus, (status, completedPart) -> {
                    log.info("Completed: PartNumber={}, etag={}", completedPart.partNumber(), completedPart.eTag());
                    (status).getCompletedParts().put(completedPart.partNumber(), completedPart);
                    return status;
                })
                .flatMap(uploadStatus1 -> completeMultipartUpload(uploadStatus))
                .handle((response, sink) -> {
                    try {
                        FileUtils.checkSdkResponse(response);
                    } catch (Exception e) {
                        sink.error(new RuntimeException(e));
                        return;
                    }
                    log.info("Upload result: {}", response.toString());

                    sink.next(new UploadResponse(filename, uploadStatus.getUploadId(), URLDecoder.decode(response.location(), StandardCharsets.UTF_8), uploadStatus.getContentType(), response.eTag()));
                });
    }

    private Mono<CompletedPart> uploadPartObject(UploadStatus uploadStatus, ByteBuffer buffer) {
        final int partNumber = uploadStatus.getAddedPartCounter();
        log.info("UploadPart - partNumber={}, contentLength={}", partNumber, buffer.capacity());

        CompletableFuture<UploadPartResponse> uploadPartResponseCompletableFuture = s3AsyncClient.uploadPart(UploadPartRequest.builder()
                        .bucket(awsConfigProperties.getBucketName())
                        .key(uploadStatus.getFileKey())
                        .partNumber(partNumber)
                        .uploadId(uploadStatus.getUploadId())
                        .contentLength((long) buffer.capacity())
                        .build(),
                AsyncRequestBody.fromPublisher(Mono.just(buffer)));

        return Mono
                .fromFuture(uploadPartResponseCompletableFuture)
                .handle((uploadPartResult, sink) -> {
                    try {
                        FileUtils.checkSdkResponse(uploadPartResult);
                    } catch (Exception e) {
                        sink.error(new RuntimeException(e));
                        return;
                    }
                    log.info("UploadPart - complete: part={}, etag={}", partNumber, uploadPartResult.eTag());
                    sink.next(CompletedPart.builder()
                            .eTag(uploadPartResult.eTag())
                            .partNumber(partNumber)
                            .build());
                });
    }

    /**
     * This method is called when a part finishes uploading. It's primary function is to verify the ETag of the part
     * we just uploaded.
     */
    private Mono<CompleteMultipartUploadResponse> completeMultipartUpload(UploadStatus uploadStatus) {
        log.info("CompleteUpload - fileKey={}, completedParts.size={}",
                uploadStatus.getFileKey(), uploadStatus.getCompletedParts().size());

        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(uploadStatus.getCompletedParts().values())
                .build();

        return Mono.fromFuture(s3AsyncClient.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(awsConfigProperties.getBucketName())
                .uploadId(uploadStatus.getUploadId())
                .multipartUpload(multipartUpload)
                .key(uploadStatus.getFileKey())
                .build()));
    }

}
