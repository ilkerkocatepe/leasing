package dev.ilkerk.leasing.infrastracture.aws;

import software.amazon.awssdk.core.SdkResponse;

public class AwsSdkUtil {

    public static boolean isErrorSdkHttpResponse(SdkResponse sdkResponse) {
        return sdkResponse.sdkHttpResponse() == null || !sdkResponse.sdkHttpResponse().isSuccessful();
    }
}
