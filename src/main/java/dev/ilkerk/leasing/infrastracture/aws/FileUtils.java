package dev.ilkerk.leasing.infrastracture.aws;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import software.amazon.awssdk.core.SdkResponse;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.List;


@UtilityClass
@Slf4j
public class FileUtils {

    private boolean isValidType(final FilePart filePart) {
        return true;
    }

    private boolean isEmpty(final FilePart filePart) {
        return StringUtils.isEmpty(filePart.filename())
                && ObjectUtils.isEmpty(filePart.headers().getContentType());
    }


    public ByteBuffer dataBufferToByteBuffer(List<DataBuffer> buffers) {
        log.info("Creating ByteBuffer from {} chunks", buffers.size());

        int partSize = 0;
        for(DataBuffer b : buffers) {
            partSize += b.readableByteCount();
        }

        ByteBuffer partData = ByteBuffer.allocate(partSize);
        buffers.forEach(buffer -> partData.put(buffer.toByteBuffer()));

        // Reset read pointer to first byte
        partData.rewind();

        log.info("PartData: capacity={}", partData.capacity());
        return partData;
    }

    public void checkSdkResponse(SdkResponse sdkResponse) throws Exception {
        if (AwsSdkUtil.isErrorSdkHttpResponse(sdkResponse)){
            throw new Exception(MessageFormat.format("{0} - {1}", sdkResponse.sdkHttpResponse().statusCode(), sdkResponse.sdkHttpResponse().statusText()));
        }
    }
    public void filePartValidator(FilePart filePart) throws Exception {
        if (isEmpty(filePart)){
            throw new Exception("File cannot be empty or null!");
        }
        if (!isValidType(filePart)){
            throw new Exception("Invalid file type");
        }
    }

}
