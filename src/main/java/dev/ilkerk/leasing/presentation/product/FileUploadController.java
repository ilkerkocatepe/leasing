package dev.ilkerk.leasing.presentation.product;

import dev.ilkerk.leasing.application.product.dto.response.UploadSuccessResponse;
import dev.ilkerk.leasing.application.product.service.FileUploadService;
import dev.ilkerk.leasing.domain.product.entity.FileDirectory;
import dev.ilkerk.leasing.infrastracture.aws.FileUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("upload")
@Slf4j
@RequiredArgsConstructor
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping
    public Mono<UploadSuccessResponse> uploadMany(@RequestPart("file") Mono<FilePart> filePart, @RequestParam FileDirectory directory) {
        return filePart.<FilePart>handle((filePart1, sink) -> {
                    try {
                        FileUtils.filePartValidator(filePart1);
                    } catch (Exception e) {
                        sink.error(new RuntimeException(e));
                        return;
                    }
                    sink.next(filePart1);
                })
                .flatMap(file -> fileUploadService.uploadObject(file, directory))
                .map((fileResponses) -> new UploadSuccessResponse(fileResponses, "Upload successfully"));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Response {
        private Object response;
    }

    Mono<byte[]> getByteArray(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> dataBuffer.asByteBuffer().array());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public  static class ResponsePayload {
        private byte[] array;
    }
}
